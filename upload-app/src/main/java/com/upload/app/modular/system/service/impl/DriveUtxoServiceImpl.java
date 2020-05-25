package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.DriveUtxoMapper;
import com.upload.app.modular.system.model.DriveUtxo;
import com.upload.app.modular.system.service.DriveUtxoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DriveUtxoServiceImpl implements DriveUtxoService {

    @Resource
    private DriveUtxoMapper driveUtxoMapper;

    @Override
    public int insert(DriveUtxo driveUtxo) {
        return driveUtxoMapper.insert(driveUtxo);
    }

    @Override
    public int delete(DriveUtxo driveUtxo) {
        return driveUtxoMapper.delete(driveUtxo);
    }

    @Override
    public DriveUtxo findByTxidAndN(String txid, Integer n) {
        return driveUtxoMapper.findByTxidAndN(txid, n);
    }

    @Override
    public DriveUtxo findByDriveId(String driveId) {
        return driveUtxoMapper.findByDriveId(driveId);
    }

}
