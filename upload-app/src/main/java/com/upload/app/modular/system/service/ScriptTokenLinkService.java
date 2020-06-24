package com.upload.app.modular.system.service;


import com.alibaba.fastjson.JSONArray;
import com.upload.app.modular.system.model.ScriptTokenLink;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface ScriptTokenLinkService {

    int insert(ScriptTokenLink scriptTokenLink);

    ScriptTokenLink findByTokenAssets(String tokenId, String txid, Integer vout);

    BigInteger selectFAToken(String tokenId, String txid, Integer vout);

    ScriptTokenLink findByTokenAssetsStatus(String txid, Integer vout, Integer status);

    List<ScriptTokenLink> selectByTxid(String txid, Integer vout);

    List<ScriptTokenLink> tokenVin(JSONArray vins);

    BigInteger findToTokenByScript(List<String> script);

    BigInteger findFromTokenByScript(List<String> script);

    BigInteger findDestructionByScript(@Param("script") List<String> script);

}
