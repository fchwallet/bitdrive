package com.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.modular.system.dao.SystemUtxoMapper;
import com.upload.app.modular.system.model.DriveUtxo;
import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.SystemUtxoService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SystemUtxoServiceImpl implements SystemUtxoService {

    @Resource
    private SystemUtxoMapper systemUtxoMapper;


    @Override
    public List<SystemUtxo> findByAddress(String address) {
        return systemUtxoMapper.findByAddress(address);
    }

    @Override
    public Boolean isExistence(String txid, Integer n) {
        return systemUtxoMapper.isExistence(txid, n);
    }

    @Override
    public int insert(SystemUtxo systemUtxo) {
        return systemUtxoMapper.insert(systemUtxo);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Map<String, Object> spendUtxo(String metadata, List<String> fchAddress, Integer size, String data, DriveUtxo du, Integer type) {

        List<CommonTxOputDto> outputs = new ArrayList<>();

        Integer metadatasize = metadata.getBytes().length;
        BigDecimal metadatasizefree = new BigDecimal(metadatasize).divide(new BigDecimal("2")).setScale(8);    // 设置8位小数
        BigDecimal metadatafee = new BigDecimal("0.00000001").multiply(metadatasizefree);
        if (metadatafee.compareTo(new BigDecimal("0.00002")) < 0)
            metadatafee = new BigDecimal("0.00004");
        else
            metadatafee = metadatafee.multiply(new BigDecimal("2"));

        fchAddress.add("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String[] address = fchAddress.toArray(new String[0]);
        CommonTxOputDto c1 = new CommonTxOputDto(address, metadatafee, metadata, 1);
        outputs.add(c1);


        BigDecimal sizefree = new BigDecimal(size).divide(new BigDecimal("2")).setScale(8);    // 设置8位小数
        BigDecimal fee = new BigDecimal("0.00000001").multiply(sizefree);

        BigDecimal sumFee = fee.add(metadatafee);

        List<SystemUtxo> systemUtxoList = systemUtxoMapper.findByAddress("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        List<TxInputDto> inputs = new ArrayList<>();
        BigDecimal v = new BigDecimal("0");                 //总的utxo钱
        BigDecimal f = sumFee.add(new BigDecimal("0.00001"));
        for (SystemUtxo systemUtxo : systemUtxoList) {
            TxInputDto input = new TxInputDto(systemUtxo.getTxid(),systemUtxo.getN(),"");
            if (v.compareTo(f) < 0 || inputs.size() == 0) {
                v = v.add(new BigDecimal(systemUtxo.getValue()));
                systemUtxoMapper.delete(systemUtxo.getTxid(), systemUtxo.getN());
                inputs.add(input);
            } else {
                break;
            }
        }

        if (type == 2) {
            TxInputDto input = new TxInputDto(du.getTxid(), du.getN(),"");
            inputs.add(input);
            v = v.add(new BigDecimal(du.getValue()));
        }

        BigDecimal fvalue = v.subtract(fee).subtract(new BigDecimal("0.00001")).subtract(metadatafee);

        String[] sysad = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
        CommonTxOputDto c2 = new CommonTxOputDto(sysad, fvalue, 2);
        outputs.add(c2);                                //找零


        CommonTxOputDto c3 = new CommonTxOputDto(data, 3);
        outputs.add(c3);                            //文件

        Map<String, Object> map = new HashedMap();

        try {

            String createHex = Api.CreateDrivetx(inputs, outputs);
            String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
            String hex = Api.SendRawTransaction(signHex);
            map.put("hex", hex);
            map.put("sumFee", sumFee);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;

    }

}
