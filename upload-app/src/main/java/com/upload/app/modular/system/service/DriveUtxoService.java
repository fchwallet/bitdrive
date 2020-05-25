package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.DriveUtxo;

public interface DriveUtxoService {

    int insert(DriveUtxo driveUtxo);

    int delete(DriveUtxo driveUtxo);

    DriveUtxo findByTxidAndN(String txid, Integer n);

    DriveUtxo findByDriveId(String driveId);

}
