package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.SystemUtxo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemUtxoMapper {

    List<SystemUtxo> findByAddress(String address);

    Boolean isExistence(@Param("txid") String txid, @Param("n") Integer n);

    int insert(SystemUtxo systemUtxo);

    int delete(@Param("txid") String txid, @Param("n") Integer n);
}
