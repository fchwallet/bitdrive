package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.DriveTxAddress;

import java.util.List;

public interface DriveTxAddressService {

    List<DriveTxAddress> findByAddress(String address);

    DriveTxAddress findByTxidAndN(String txid, String n);

    int insert(DriveTxAddress driveTxAddress);

    DriveTxAddress findByDrive(String address, String driveId);

    List<DriveTxAddress> findUpdateByDriveList(String address, String driveId);

    DriveTxAddress findUpdate(String address, String updateId);

    Long findByTxidCount(String txid);

}
