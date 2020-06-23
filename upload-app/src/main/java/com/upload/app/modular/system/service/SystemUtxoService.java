package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.DriveUtxo;
import com.upload.app.modular.system.model.SystemUtxo;

import java.util.List;
import java.util.Map;

public interface SystemUtxoService {

    List<SystemUtxo> findByAddress(String address);

    Boolean isExistence(String txid, Integer n);

    int insert(SystemUtxo systemUtxo);

    Map<String, Object> spendUtxo(String metadata, List<String> fchAddress, Integer size, String data, DriveUtxo du, Integer type) throws InterruptedException;

    Boolean terminateDrive(String address, String driveId) throws Exception;

    int delete(String txid, Integer n);

}
