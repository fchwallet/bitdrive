package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.ScriptTokenLink;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface ScriptTokenLinkMapper {

    int insert(ScriptTokenLink scriptTokenLink);

    ScriptTokenLink findByTokenAssets(@Param("tokenId") String tokenId, @Param("txid") String txid, @Param("vout") Integer vout);

    BigInteger selectFAToken(@Param("tokenId") String tokenId, @Param("txid") String txid, @Param("vout") Integer vout);

    ScriptTokenLink findByTokenAssetsStatus(@Param("txid") String txid, @Param("vout") Integer vout, @Param("status") Integer status);

    List<ScriptTokenLink> selectByTxid(@Param("txid")String txid, @Param("vout") Integer vout);

    BigInteger findToTokenByScript(@Param("script") List<String> script);

    BigInteger findFromTokenByScript(@Param("script") List<String> script);

    BigInteger findDestructionByScript(@Param("script") List<String> script);

    BigInteger selectFASumToken(@Param("script") List<String> script, @Param("address") String address, @Param("tokenId") String tokenId);

}
