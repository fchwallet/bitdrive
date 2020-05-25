package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.DriveTxAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriveTxAddressMapper {

    List<DriveTxAddress> findByAddress(String address);

    DriveTxAddress findByTxidAndN(@Param("txid") String txid, @Param("n") String n);

    int insert(DriveTxAddress driveTxAddress);

    DriveTxAddress findByDrive(@Param("address") String address, @Param("driveId") String driveId);

    List<DriveTxAddress> findUpdateByDriveList(@Param("address") String address, @Param("driveId") String driveId);

    DriveTxAddress findUpdate(@Param("address") String address, @Param("updateId") String updateId);

    Long findByTxidCount(String txid);

}
