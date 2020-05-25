package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.DriveTxAddressMapper;
import com.upload.app.modular.system.model.DriveTxAddress;
import com.upload.app.modular.system.service.DriveTxAddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DriveTxAddressServiceImpl implements DriveTxAddressService {

    @Resource
    private DriveTxAddressMapper driveTxAddressMapper;

    @Override
    public List<DriveTxAddress> findByAddress(String address) {
        return driveTxAddressMapper.findByAddress(address);
    }

    @Override
    public DriveTxAddress findByTxidAndN(String txid, String n) {
        return driveTxAddressMapper.findByTxidAndN(txid, n);
    }

    @Override
    public int insert(DriveTxAddress driveTxAddress) {
        return driveTxAddressMapper.insert(driveTxAddress);
    }

    @Override
    public DriveTxAddress findByDrive(String address, String driveId) {
        return driveTxAddressMapper.findByDrive(address, driveId);
    }

    @Override
    public List<DriveTxAddress> findUpdateByDriveList(String address, String driveId) {
        return driveTxAddressMapper.findUpdateByDriveList(address, driveId);
    }

    @Override
    public DriveTxAddress findUpdate(String address, String updateId) {
        return driveTxAddressMapper.findUpdate(address, updateId);
    }

    @Override
    public Long findByTxidCount(String txid) {
        return driveTxAddressMapper.findByTxidCount(txid);
    }

}
