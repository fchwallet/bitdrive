package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.DriveUtxo;
import org.apache.ibatis.annotations.Param;

public interface DriveUtxoMapper {

    int insert(DriveUtxo driveUtxo);

    int delete(DriveUtxo driveUtxo);

    DriveUtxo findByTxidAndN(@Param("txid") String txid, @Param("n") Integer n);

    DriveUtxo findByDriveId(String driveId);

}
