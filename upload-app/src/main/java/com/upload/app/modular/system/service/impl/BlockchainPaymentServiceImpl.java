package com.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.modular.system.model.BalanceHistory;
import com.upload.app.modular.system.model.FchXsvLink;
import com.upload.app.modular.system.model.ScriptUtxoTokenLink;
import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BlockchainPaymentServiceImpl implements BlockchainPaymentService {

    final String tokenId = "c7f7c99fb2f9ad865ba17f702dc21e2643ac1562941888952a27aa399e261101";

    final String systemAddress = "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx";

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


    @Override
    public Boolean payment(String fchAddress, Integer type, String methodName) throws Exception {

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(fchAddress);

        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());

        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);

        BigInteger balance = toAssets.subtract(fromAssets);

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash(), tokenId);

        BigInteger sumAmount = new BigInteger("0");

        List<TxInputDto> inputs = new ArrayList<>();

        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            TxInputDto tx = new TxInputDto(sut.getTxid(), sut.getN(),"");
            inputs.add(tx);
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            sumAmount = sumAmount.add(amount);
        }

        if (balance.compareTo(sumAmount) != 0) {
            //token余额和链上不对应
            return null;
        }

        BigInteger newAmount = sumAmount.divide(new BigInteger("100000000"));

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

            CommonTxOputDto c1 = new CommonTxOputDto(sysAddress, new BigDecimal("0.0001"), agreement + "0000000077359400", 1);           //扣钱 2块
            outputs.add(c1);
            subAmount = newAmount.subtract(new BigInteger("2"));
            balanceHistory.setChange(-2);
        }


        if (subAmount.compareTo(new BigInteger("0")) > 0) {

            subAmount = subAmount.multiply(new BigInteger("100000000"));

            String mintQuantityHex = new BigInteger(subAmount.toString(),10).toString(16);

            while (true) {
                if (mintQuantityHex.length() >= 16) {
                    break;
                } else {
                    mintQuantityHex = "0"+mintQuantityHex;
                }
            }

            String[] givechangeAddress = {fchXsvLink.getXsvAddress(), systemAddress};
            CommonTxOputDto c2 = new CommonTxOputDto(givechangeAddress, new BigDecimal("0.0001"), agreement+mintQuantityHex, 1);                              // 多余找零
            outputs.add(c2);

        }

        List<SystemUtxo> sysUtxoList = systemUtxoService.findByAddress(systemAddress);
        BigDecimal sysFee = new BigDecimal("0");
        for (SystemUtxo sysUtxo : sysUtxoList) {

            if (sysFee.compareTo(new BigDecimal("0.001")) < 0) {
                sysFee = sysFee.add(new BigDecimal(sysUtxo.getValue()));
                TxInputDto tx = new TxInputDto(sysUtxo.getTxid(), sysUtxo.getN(),"");
                inputs.add(tx);
                systemUtxoService.delete(sysUtxo.getTxid(), sysUtxo.getN());
            } else
                break;
        }
        sysFee = sysFee.subtract(new BigDecimal("0.0002"));
        CommonTxOputDto c3 = new CommonTxOputDto(sysAddress, sysFee, 2);
        outputs.add(c3);


        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String hex = Api.SendRawTransaction(signHex);

        if (!StringUtils.isEmpty(hex)) {
//            JSONObject json = Api.GetRawTransaction(hex);
//            JSONArray vouts = json.getJSONArray("vout");
//            for (Object o : vouts) {
//                JSONObject v = (JSONObject)o;
//                Integer n = v.getInteger("n");
//                if (n == 0) {
//
//                } else if (n == 1) {
//
//                }
//            }
            balanceHistory.setAddress(fchAddress);
            balanceHistory.setType(methodName);
            balanceHistory.setTimestamp(new Date());
            balanceHistoryService.insert(balanceHistory);
            return true;
        } else
            return false;

    }


}
