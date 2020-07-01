package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.ScriptUtxoTokenLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScriptUtxoTokenLinkMapper {

    int insert(ScriptUtxoTokenLink scriptUtxoTokenLink);

    int deleteUtxoToken(@Param("txid") String txid, @Param("n") Integer n);

    List<ScriptUtxoTokenLink> findListByScript(@Param("script") List<String> script, @Param("addressHash") String addressHash, @Param("tokenId") String tokenId);

    ScriptUtxoTokenLink findUtxoToken(@Param("txid") String txid, @Param("n") Integer n);

    List<ScriptUtxoTokenLink> findAllList();

}
