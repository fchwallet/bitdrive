package com.upload.app.modular.system.service.impl;

import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.modular.system.model.*;
import com.upload.app.modular.system.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class SendServiceImpl implements SendService {

    @Resource
    private SystemUtxoService systemUtxoService;

    @Resource
    private FchXsvLinkService fchXsvLinkService;

    @Resource
    private AddressScriptLinkService addressScriptLinkService;

    @Resource
    private ScriptTokenLinkService scriptTokenLinkService;

    @Resource
    private ScriptUtxoTokenLinkService scriptUtxoTokenLinkService;

    @Resource
    private BlockingQueueService blockingQueueService;

    @Value("${sys.tokenAddress}")
    private String sysTokenAddress;

    @Value("${sys.address}")
    private String systemAddress;

    final String agreement = "06534c502b2b000202010453454e4420c7f7c99fb2f9ad865ba17f702dc21e2643ac1562941888952a27aa399e26110108";

    @Value("${sys.tokenId}")
    private String tokenId;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Boolean Create(String address, String value) throws Exception {

        List<TxInputDto> inputs = new ArrayList<>();

        BigDecimal sysFee = new BigDecimal("0");
        Double count = Double.valueOf(value) / 100;
        Double fee = count * 0.000002 + 0.001;
        while (true) {
            if (sysFee.compareTo(new BigDecimal(fee)) < 0) {
                SystemUtxo systemUtxo = blockingQueueService.take();
                TxInputDto input = new TxInputDto(systemUtxo.getTxid(), systemUtxo.getN(), "");
                sysFee = sysFee.add(new BigDecimal(systemUtxo.getValue()));
                systemUtxoService.delete(systemUtxo.getTxid(), systemUtxo.getN());
                inputs.add(input);
            } else {
                break;
            }
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(sysTokenAddress);

        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());

        if (scriptList == null || scriptList.size() < 1 ) {
            return false;
        }

        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);
        BigInteger destructionAssets = scriptTokenLinkService.findDestructionByScript(scriptList);
        BigInteger balance = toAssets.subtract(fromAssets).subtract(destructionAssets);

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash(), tokenId);
        BigInteger sumAmount = new BigInteger("0");

        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            TxInputDto tx = new TxInputDto(sut.getTxid(), sut.getN(),"");
            inputs.add(tx);
            scriptUtxoTokenLinkService.deleteUtxoToken(sut.getTxid(), sut.getN());
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            sumAmount = sumAmount.add(amount);
        }


        if (balance.compareTo(sumAmount) != 0) {
            //token余额和链上不对应
            return false;
        }

        BigInteger newAmount = sumAmount.divide(new BigInteger("100000000"));

        BigInteger newValue = new BigInteger(value);

        if (newAmount.compareTo(newValue) < 0) {
            return false;
        }

        BigInteger surplus = newAmount.subtract(newValue);              //剩余找零

        String surplusHex = new BigInteger(surplus.multiply(new BigInteger("100000000")).toString(),10).toString(16);         //找零16进制

        while (true) {
            if (surplusHex.length() >= 16) {
                break;
            } else {
                surplusHex = "0"+surplusHex;
            }
        }


        List<CommonTxOputDto> outputs = new ArrayList<>();

        FchXsvLink toFchXsvLink = fchXsvLinkService.findByFch(address);
        String[] givechangeAddress2 = {toFchXsvLink.getXsvAddress(), systemAddress};

        BigInteger v = (new BigDecimal("100")).multiply(new BigDecimal("100000000")).toBigInteger();
        String[] givechangeAddress1 = {systemAddress, sysTokenAddress};
        CommonTxOputDto c1 = new CommonTxOputDto(givechangeAddress1, new BigDecimal("0.0001"), agreement+surplusHex, 1);                              // 多余找零
        outputs.add(c1);

        for (int i = 0; i < count; i++) {
            String newValueHex = v.toString(16);
            while (true) {
                if (newValueHex.length() >= 16) {
                    break;
                } else {
                    newValueHex = "0"+newValueHex;
                }
            }

            CommonTxOputDto c2 = new CommonTxOputDto(givechangeAddress2, new BigDecimal("0.00006"), agreement+newValueHex, 1);                              //给充值地址打钱
            outputs.add(c2);

        }


        BigDecimal f1 = new BigDecimal(count).multiply(new BigDecimal("0.00002")).setScale(8, BigDecimal.ROUND_HALF_UP);
        BigDecimal f2 = new BigDecimal(count).multiply(new BigDecimal("0.00006"));

        sysFee = sysFee.subtract(new BigDecimal("0.0002")).subtract(f1).subtract(f2).setScale(8, BigDecimal.ROUND_HALF_UP);

        String[] sysAddress = {systemAddress};
        CommonTxOputDto c3 = new CommonTxOputDto(sysAddress, sysFee, 2);
        outputs.add(c3);

        CommonTxOputDto c4 = new CommonTxOputDto("64726976656368723031", 3);                    // 充值
        outputs.add(c4);

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, systemAddress);
        String hex = Api.SendRawTransaction(signHex);

        if (StringUtils.isEmpty(hex))
            return false;
        else {

            StringBuilder script = new StringBuilder();
            script.append("6376a91484be1e524ff4324816f25e558dd89be1a29841b388ac6776a914");
            script.append(toFchXsvLink.getAddressHash()+"88ac");

            StringBuilder fromscript = new StringBuilder();
            fromscript.append("6376a91484be1e524ff4324816f25e558dd89be1a29841b388ac6776a91457353d54a4fc0c2d24ef12f27c4351f35885541688ac");

            List<String> Lcount = addressScriptLinkService.findByScript(script.toString());

            if (Lcount == null || Lcount.size() < 1) {

                AddressScriptLink asl3 = new AddressScriptLink();
                asl3.setAddress("84be1e524ff4324816f25e558dd89be1a29841b3");
                asl3.setScript(script.toString());
                addressScriptLinkService.insert(asl3);
                AddressScriptLink asl4 = new AddressScriptLink();
                asl4.setAddress(toFchXsvLink.getAddressHash());
                asl4.setScript(script.toString());
                addressScriptLinkService.insert(asl4);

            }

            ScriptUtxoTokenLink sutl1 = new ScriptUtxoTokenLink();                             // 找零
            sutl1.setTokenId(tokenId);
            sutl1.setScript(fromscript.toString());
            sutl1.setAddress("57353d54a4fc0c2d24ef12f27c4351f358855416");
            sutl1.setTxid(hex);
            sutl1.setN(0);
            sutl1.setValue("0.0001");
            scriptUtxoTokenLinkService.insert(sutl1);

            ScriptUtxoTokenLink sutl2 = new ScriptUtxoTokenLink();                              // 找零
            sutl2.setTokenId(tokenId);
            sutl2.setScript(fromscript.toString());
            sutl2.setAddress("84be1e524ff4324816f25e558dd89be1a29841b3");
            sutl2.setTxid(hex);
            sutl2.setN(0);
            sutl2.setValue("0.0001");
            scriptUtxoTokenLinkService.insert(sutl2);

            ScriptTokenLink stl2 = new ScriptTokenLink();
            stl2.setScript(fromscript.toString());
            stl2.setToken(surplus.multiply(new BigInteger("100000000")));
            stl2.setFromScript(fromscript.toString());
            stl2.setStatus(4);
            stl2.setTokenId(tokenId);
            stl2.setTxid(hex);
            stl2.setVout(0);
            scriptTokenLinkService.insert(stl2);

            SystemUtxo systemUtxo = new SystemUtxo();
            systemUtxo.setAddress(systemAddress);
            systemUtxo.setValue(sysFee.toString());
            systemUtxo.setN(count.intValue()+1);
            systemUtxo.setTxid(hex);
            systemUtxoService.insert(systemUtxo);

            for (int i = 1; i <= count; i++) {

                ScriptUtxoTokenLink sutl3 = new ScriptUtxoTokenLink();                              // 打钱
                sutl3.setTokenId(tokenId);
                sutl3.setScript(script.toString());
                sutl3.setAddress("84be1e524ff4324816f25e558dd89be1a29841b3");
                sutl3.setTxid(hex);
                sutl3.setN(i);
                sutl3.setValue("0.00006");
                scriptUtxoTokenLinkService.insert(sutl3);

                ScriptUtxoTokenLink sutl4 = new ScriptUtxoTokenLink();                              // 打钱
                sutl4.setTokenId(tokenId);
                sutl4.setScript(script.toString());
                sutl4.setAddress(toFchXsvLink.getAddressHash());
                sutl4.setTxid(hex);
                sutl4.setN(i);
                sutl4.setValue("0.00006");
                scriptUtxoTokenLinkService.insert(sutl4);

                ScriptTokenLink stl1 = new ScriptTokenLink();                                       //打给他
                stl1.setScript(script.toString());
                stl1.setToken(v);
                stl1.setFromScript(fromscript.toString());
                stl1.setStatus(2);
                stl1.setTokenId(tokenId);
                stl1.setTxid(hex);
                stl1.setVout(i);
                scriptTokenLinkService.insert(stl1);


            }

            return true;
        }
    }

}
