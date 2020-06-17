package com.upload.app.modular.system.service.impl;

import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.modular.system.dao.DriveUtxoMapper;
import com.upload.app.modular.system.dao.SystemUtxoMapper;
import com.upload.app.modular.system.model.Create;
import com.upload.app.modular.system.model.DriveUtxo;
import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.CreateService;
import com.upload.app.modular.system.service.SystemUtxoService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private DriveUtxoMapper driveUtxoMapper;

    @Resource
    private SystemUtxoService systemUtxoService;

    @Resource
    private CreateService createService;

    final String systemAddress = "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx";

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
            metadatafee = new BigDecimal("0.00002");
        else
            metadatafee = metadatafee;

        fchAddress.add("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String[] address = fchAddress.toArray(new String[0]);

        BigDecimal sizefree = new BigDecimal(size).divide(new BigDecimal("2")).setScale(8);    // 设置8位小数
        BigDecimal fee = new BigDecimal("0.00000001").multiply(sizefree);

        BigDecimal sumFee = fee.add(metadatafee);

        List<SystemUtxo> systemUtxoList = systemUtxoMapper.findByAddress(systemAddress);
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

        BigDecimal metadataNum = (metadatafee.add(fee).add(new BigDecimal("0.00001"))).multiply(new BigDecimal("3"));
        BigDecimal fvalue = v.subtract(fee).subtract(new BigDecimal("0.00001")).subtract(metadatafee).subtract(metadataNum);

        CommonTxOputDto c1 = new CommonTxOputDto(address, metadataNum, metadata, 1);
        outputs.add(c1);
        String[] sysad = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
        CommonTxOputDto c2 = new CommonTxOputDto(sysad, fvalue, 2);
        outputs.add(c2);                                //找零


        CommonTxOputDto c3 = new CommonTxOputDto(data, 3);
        outputs.add(c3);                            //文件

        Map<String, Object> map = new HashedMap();

        try {

            String createHex = Api.CreateDrivetx(inputs, outputs);
            String signHex = Api.SignDrivetx(createHex, systemAddress);
            String hex = Api.SendRawTransaction(signHex);
            map.put("hex", hex);
            map.put("sumFee", sumFee);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;

    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Boolean terminateDrive(String address, String driveId) throws Exception {

        List<CommonTxOputDto> outputs = new ArrayList<>();

        List<SystemUtxo> systemUtxoList = systemUtxoMapper.findByAddress(systemAddress);
        List<TxInputDto> inputs = new ArrayList<>();
        BigDecimal sysFee = new BigDecimal("0");
        for (SystemUtxo sysUtxo : systemUtxoList) {
            if (sysFee.compareTo(new BigDecimal("0.001")) < 0) {
                sysFee = sysFee.add(new BigDecimal(sysUtxo.getValue()));
                TxInputDto tx = new TxInputDto(sysUtxo.getTxid(), sysUtxo.getN(),"");
                inputs.add(tx);
                systemUtxoService.delete(sysUtxo.getTxid(), sysUtxo.getN());
            } else
                break;
        }


        DriveUtxo driveUtxo = driveUtxoMapper.findByDriveId(driveId);
        if (driveUtxo == null)
            return false;

        TxInputDto tx = new TxInputDto(driveUtxo.getTxid(), driveUtxo.getN(),"");
        inputs.add(tx);


        String[] ads = {"1111111111111111111114oLvT2"};
        CommonTxOputDto c1 = new CommonTxOputDto(ads, new BigDecimal("0.00001"),2);
        outputs.add(c1);
        String[] sysad = {systemAddress};
        sysFee = sysFee.subtract(new BigDecimal("0.00001"));
        CommonTxOputDto c2 = new CommonTxOputDto(sysad, sysFee, 2);
        outputs.add(c2);                                //找零

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, systemAddress);
        String hex = Api.SendRawTransaction(signHex);
        if (!StringUtils.isEmpty(hex)) {
            Create create = new Create();
            create.setStatus(1);
            create.setDriveId(driveId);
            createService.update(create);
            DriveUtxo delete = new DriveUtxo();
            delete.setDriveId(driveId);
            delete.setTxid(driveUtxo.getTxid());
            delete.setN(driveUtxo.getN());
            driveUtxoMapper.delete(delete);
            return true;
        } else
            return false;

    }

    @Override
    public int delete(String txid, Integer n) {
        return systemUtxoMapper.delete(txid, n);
    }

}
