package com.upload.app.modular.system.service.impl;

import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.modular.system.model.FchXsvLink;
import com.upload.app.modular.system.model.ScriptUtxoTokenLink;
import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.*;
import org.apache.commons.lang3.StringUtils;
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

    final String payAddress = "18x7ZqhUHV3NgCGAw3NPEsGbEZ5i6beyD6";

    final String systemAddress = "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx";

    final String agreement = "06534c502b2b000202010453454e4420c7f7c99fb2f9ad865ba17f702dc21e2643ac1562941888952a27aa399e26110108";

    final String tokenId = "c7f7c99fb2f9ad865ba17f702dc21e2643ac1562941888952a27aa399e261101";

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Boolean Create(String address, String value) throws Exception {

        List<TxInputDto> inputs = new ArrayList<>();

        List<SystemUtxo> sysUtxoList = systemUtxoService.findByAddress(systemAddress);
        BigDecimal sysFee = new BigDecimal("0");
        for (SystemUtxo sysUtxo : sysUtxoList) {
            if (sysFee.compareTo(new BigDecimal("0.0001")) < 0) {
                sysFee = sysFee.add(new BigDecimal(sysUtxo.getValue()));
                TxInputDto tx = new TxInputDto(sysUtxo.getTxid(), sysUtxo.getN(),"");
                inputs.add(tx);
                systemUtxoService.delete(sysUtxo.getTxid(), sysUtxo.getN());
            } else
                break;
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(payAddress);

        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());

        if (scriptList == null || scriptList.size() < 1 ) {
            return false;
        }

        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);

        BigInteger balance = toAssets.subtract(fromAssets);

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash());
        BigInteger sumAmount = new BigInteger("0");

        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            TxInputDto tx = new TxInputDto(sut.getTxid(), sut.getN(),"");
            inputs.add(tx);
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

        String newValueHex =  newValue.multiply(new BigInteger("100000000")).toString(16);

        while (true) {
            if (newValueHex.length() >= 16) {
                break;
            } else {
                newValueHex = "0"+newValueHex;
            }
        }

        List<CommonTxOputDto> outputs = new ArrayList<>();

        String[] givechangeAddress1 = {systemAddress, payAddress};
        CommonTxOputDto c1 = new CommonTxOputDto(givechangeAddress1, new BigDecimal("0.0001"), agreement+surplusHex, 1);                              // 多余找零
        outputs.add(c1);

        FchXsvLink toFchXsvLink = fchXsvLinkService.findByFch(address);
        String[] givechangeAddress2 = {toFchXsvLink.getXsvAddress(), systemAddress};
        CommonTxOputDto c2 = new CommonTxOputDto(givechangeAddress2, new BigDecimal("0.0001"), agreement+newValueHex, 1);                              //给充值地址打钱
        outputs.add(c2);

        sysFee = sysFee.subtract(new BigDecimal("0.0002"));
        String[] sysAddress = {systemAddress};
        CommonTxOputDto c3 = new CommonTxOputDto(sysAddress, sysFee, 2);
        outputs.add(c3);

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String hex = Api.SendRawTransaction(signHex);

        if (StringUtils.isEmpty(hex))
            return false;
        else
            return true;

    }

}
