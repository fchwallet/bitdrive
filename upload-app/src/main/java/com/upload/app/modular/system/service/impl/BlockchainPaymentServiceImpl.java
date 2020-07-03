package com.upload.app.modular.system.service.impl;

import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.modular.system.model.*;
import com.upload.app.modular.system.service.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BlockchainPaymentServiceImpl implements BlockchainPaymentService {

    @Value("${sys.tokenId}")
    private String tokenId;

    @Value("${sys.address}")
    private String systemAddress;

    final String agreement = "06534c502b2b000202010453454e4420c7f7c99fb2f9ad865ba17f702dc21e2643ac1562941888952a27aa399e26110108";

    @Resource
    private FchXsvLinkService fchXsvLinkService;

    @Resource
    private AddressScriptLinkService addressScriptLinkService;

    @Resource
    private ScriptTokenLinkService scriptTokenLinkService;

    @Resource
    private ScriptUtxoTokenLinkService scriptUtxoTokenLinkService;

    @Resource
    private SystemUtxoService systemUtxoService;

    @Resource
    private BalanceHistoryService balanceHistoryService;

    @Resource
    private BlockingQueueService blockingQueueService;

    @Resource
    private RedisQueueService redisQueueService;

    volatile ThreadLocal<BigInteger> sumAmount = new ThreadLocal<>();

    final Object lock = new Object();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean payment(String fchAddress, Integer type, String methodName) throws Exception {

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(fchAddress);

        List<TxInputDto> inputs = new ArrayList<>();

        sumAmount.set(new BigInteger("0"));

        Map<String, Object> m;

        synchronized (lock) {
           m = sumAmount(sumAmount.get(), fchXsvLink.getAddressHash(), inputs, type);
        }

        BigInteger sumA = (BigInteger) m.get("sumAmount");
        inputs = (List<TxInputDto>)m.get("inputs");

        BigInteger newAmount = sumA.divide(new BigInteger("100000000"));

        List<CommonTxOputDto> outputs = new ArrayList<>();
        String[] sysAddress = {systemAddress, "16C3CfUHFqxnSKL3DBcGXQxoWe9L6Rf2iN"};
        BigInteger subAmount = new BigInteger("0");
        BalanceHistory balanceHistory = new BalanceHistory();

        if (type == 1) {

            if (newAmount.compareTo(new BigInteger("10")) < 0) {
                //余额不够
                return null;
            }

            CommonTxOputDto c1 = new CommonTxOputDto(sysAddress, new BigDecimal("0.0001"), agreement + "000000003b9aca00", 1);           //扣钱 10块
            outputs.add(c1);
            subAmount = newAmount.subtract(new BigInteger("10"));
            balanceHistory.setChange(-10);

        } else if (type == 2) {

            if (newAmount.compareTo(new BigInteger("2")) < 0) {
                //余额不够
                return null;
            }

            CommonTxOputDto c1 = new CommonTxOputDto(sysAddress, new BigDecimal("0.0001"), agreement + "000000000bebc200", 1);           //扣钱 2块
            outputs.add(c1);
            subAmount = newAmount.subtract(new BigInteger("2"));
            balanceHistory.setChange(-2);
        }


        if (subAmount.compareTo(new BigInteger("0")) > 0) {

            subAmount = subAmount.multiply(new BigInteger("100000000"));

            String mintQuantityHex = new BigInteger(subAmount.toString(), 10).toString(16);

            while (true) {
                if (mintQuantityHex.length() >= 16) {
                    break;
                } else {
                    mintQuantityHex = "0" + mintQuantityHex;
                }
            }

            String[] givechangeAddress = {systemAddress, fchXsvLink.getXsvAddress()};
            CommonTxOputDto c2 = new CommonTxOputDto(givechangeAddress, new BigDecimal("0.0001"), agreement + mintQuantityHex, 1);                              // 多余找零
            outputs.add(c2);

        }

        BigDecimal sysFee = new BigDecimal("0");

        while (true) {
            if (sysFee.compareTo(new BigDecimal("0.001")) < 0) {
                SystemUtxo systemUtxo = blockingQueueService.take();
                TxInputDto input = new TxInputDto(systemUtxo.getTxid(), systemUtxo.getN(), "");
                sysFee = sysFee.add(new BigDecimal(systemUtxo.getValue()));
                systemUtxoService.delete(systemUtxo.getTxid(), systemUtxo.getN());
                inputs.add(input);
            } else {
                break;
            }
        }


        sysFee = sysFee.subtract(new BigDecimal("0.0002"));
        String[] sys = {systemAddress};
        CommonTxOputDto c3 = new CommonTxOputDto(sys, sysFee, 2);
        outputs.add(c3);

        CommonTxOputDto c4 = null;
        if ("get".equals(methodName)) {
            c4 = new CommonTxOputDto("64726976656765743031",3);
        } else if ("put".equals(methodName)) {
            c4 = new CommonTxOputDto("64726976657075743031",3);
        } else if ("update".equals(methodName)) {
            c4 = new CommonTxOputDto("64726976657570643031",3);
        } else if ("list_drive_id".equals(methodName)) {
            c4 = new CommonTxOputDto("64726976656469643031",3);
        } else if ("terminate_drive_id".equals(methodName)) {
            c4 = new CommonTxOputDto("64726976657464693031",3);
        }
        outputs.add(c4);

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, systemAddress);
        String hex = Api.SendRawTransaction(signHex);

        if (!StringUtils.isEmpty(hex)) {

            StringBuilder script = new StringBuilder();
            script.append("6376a91484be1e524ff4324816f25e558dd89be1a29841b388ac6776a914");
            script.append(fchXsvLink.getAddressHash()+"88ac");

            StringBuilder fromscript = new StringBuilder();
            fromscript.append("6376a91484be1e524ff4324816f25e558dd89be1a29841b388ac6776a914");
            fromscript.append(fchXsvLink.getAddressHash()+"88ac");

            List<String> Lcount = addressScriptLinkService.findByScript(script.toString());

            if (Lcount == null || Lcount.size() < 1) {
                AddressScriptLink asl1 = new AddressScriptLink();
                AddressScriptLink asl2 = new AddressScriptLink();
                asl1.setAddress("84be1e524ff4324816f25e558dd89be1a29841b3");
                asl1.setScript(script.toString());
                asl2.setAddress(fchXsvLink.getAddressHash());
                asl2.setScript(script.toString());
                addressScriptLinkService.insert(asl1);
                addressScriptLinkService.insert(asl2);
            }

            ScriptUtxoTokenLink sutl1 = new ScriptUtxoTokenLink();
            sutl1.setTokenId(tokenId);
            sutl1.setScript(script.toString());
            sutl1.setAddress("38ef0ab8712d84cd1bf35a4da7ca42d3002951ae");
            sutl1.setTxid(hex);
            sutl1.setN(0);
            sutl1.setValue("0.0001");
            scriptUtxoTokenLinkService.insert(sutl1);

            ScriptUtxoTokenLink sutl2 = new ScriptUtxoTokenLink();
            sutl2.setTokenId(tokenId);
            sutl2.setScript(script.toString());
            sutl2.setAddress("84be1e524ff4324816f25e558dd89be1a29841b3");
            sutl2.setTxid(hex);
            sutl2.setN(0);
            sutl2.setValue("0.0001");
            scriptUtxoTokenLinkService.insert(sutl2);

            ScriptUtxoTokenLink sutl3 = new ScriptUtxoTokenLink();
            sutl3.setTokenId(tokenId);
            sutl3.setScript(script.toString());
            sutl3.setAddress("84be1e524ff4324816f25e558dd89be1a29841b3");
            sutl3.setTxid(hex);
            sutl3.setN(1);
            sutl3.setValue("0.0001");
            scriptUtxoTokenLinkService.insert(sutl3);

            ScriptUtxoTokenLink sutl4 = new ScriptUtxoTokenLink();
            sutl4.setTokenId(tokenId);
            sutl4.setScript(script.toString());
            sutl4.setAddress(fchXsvLink.getAddressHash());
            sutl4.setTxid(hex);
            sutl4.setN(1);
            sutl4.setValue("0.0001");
            scriptUtxoTokenLinkService.insert(sutl4);

            redisQueueService.lpush(sutl1);
            redisQueueService.lpush(sutl2);
            redisQueueService.lpush(sutl3);
            redisQueueService.lpush(sutl4);


            ScriptTokenLink stl1 = new ScriptTokenLink();
            stl1.setScript("6376a91484be1e524ff4324816f25e558dd89be1a29841b388ac6776a91438ef0ab8712d84cd1bf35a4da7ca42d3002951ae88ac");
            if (type == 1)
                stl1.setToken(new BigInteger("1000000000"));
            else if (type == 2)
                stl1.setToken(new BigInteger("200000000"));
            stl1.setFromScript(script.toString());
            stl1.setStatus(2);
            stl1.setTokenId(tokenId);
            stl1.setTxid(hex);
            stl1.setVout(0);
            scriptTokenLinkService.insert(stl1);

            ScriptTokenLink stl2 = new ScriptTokenLink();
            stl2.setScript(script.toString());
            stl2.setToken(subAmount);
            stl2.setFromScript(script.toString());
            stl2.setStatus(4);
            stl2.setTokenId(tokenId);
            stl2.setTxid(hex);
            stl2.setVout(1);
            scriptTokenLinkService.insert(stl2);

            balanceHistory.setAddress(fchAddress);
            balanceHistory.setType(methodName);
            balanceHistory.setTimestamp(new Date());
            balanceHistoryService.insert(balanceHistory);

            SystemUtxo systemUtxo = new SystemUtxo();
            systemUtxo.setAddress(systemAddress);
            systemUtxo.setValue(sysFee.toString());
            systemUtxo.setN(2);
            systemUtxo.setTxid(hex);
            systemUtxoService.insert(systemUtxo);

            return true;
        } else
            return false;

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized Map<String, Object> sumAmount(BigInteger sumAmount, String address, List<TxInputDto> inputs, Integer type) {

        Map<String, Object> map = new HashedMap();

        if (type == 1) {

            if (sumAmount.compareTo(new BigInteger("1000000000")) < 0) {
                ScriptUtxoTokenLink sut = redisQueueService.blpop(address);
                TxInputDto tx = new TxInputDto(sut.getTxid(), sut.getN(), "");
                scriptUtxoTokenLinkService.deleteUtxoToken(sut.getTxid(), sut.getN());
                inputs.add(tx);
                BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
                BigInteger sum = sumAmount.add(amount);
                return sumAmount(sum, address, inputs, type);
            } else {
                map.put("sumAmount", sumAmount);
                map.put("inputs", inputs);
                return map;
            }

        } else if (type == 2) {

            if (sumAmount.compareTo(new BigInteger("200000000")) < 0) {

                ScriptUtxoTokenLink sut = redisQueueService.blpop(address);
                TxInputDto tx = new TxInputDto(sut.getTxid(), sut.getN(), "");
                scriptUtxoTokenLinkService.deleteUtxoToken(sut.getTxid(), sut.getN());
                inputs.add(tx);
                BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
                BigInteger sum = sumAmount.add(amount);
                return sumAmount(sum, address, inputs, type);

            } else {
                map.put("sumAmount", sumAmount);
                map.put("inputs", inputs);
                return map;
            }

        }

        return map;

    }


}
