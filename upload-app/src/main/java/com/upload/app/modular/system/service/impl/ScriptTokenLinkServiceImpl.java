package com.upload.app.modular.system.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.modular.system.dao.ScriptTokenLinkMapper;
import com.upload.app.modular.system.model.ScriptTokenLink;
import com.upload.app.modular.system.service.ScriptTokenLinkService;
import com.upload.app.modular.system.service.ScriptUtxoTokenLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScriptTokenLinkServiceImpl implements ScriptTokenLinkService {

    @Resource
    private ScriptTokenLinkMapper scriptTokenLinkMapper;

    @Resource
    private ScriptUtxoTokenLinkService scriptUtxoTokenLinkService;

    @Override
    public int insert(ScriptTokenLink scriptTokenLink) {
        return scriptTokenLinkMapper.insert(scriptTokenLink);
    }

    @Override
    public ScriptTokenLink findByTokenAssets(String tokenId, String txid, Integer vout) {
        return scriptTokenLinkMapper.findByTokenAssets(tokenId, txid, vout);
    }

    @Override
    public BigInteger selectFAToken(String tokenId, String txid, Integer vout) {
        return scriptTokenLinkMapper.selectFAToken(tokenId, txid, vout);
    }

    @Override
    public ScriptTokenLink findByTokenAssetsStatus(String txid, Integer vout, Integer status) {
        return scriptTokenLinkMapper.findByTokenAssetsStatus(txid, vout, status);
    }

    @Override
    public List<ScriptTokenLink> selectByTxid(String txid, Integer vout) {
        return scriptTokenLinkMapper.selectByTxid(txid, vout);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public List<ScriptTokenLink> tokenVin(JSONArray vins) {

        try {
            List<ScriptTokenLink> tokenAssetsList = new ArrayList<>();

            for (Object v : vins) {

                JSONObject vin = (JSONObject) v;
                String txid = vin.getString("txid");
                Integer vout = vin.getInteger("vout");
                scriptUtxoTokenLinkService.deleteUtxoToken(txid, vout);
                ScriptTokenLink tokenAssets = scriptTokenLinkMapper.findByTokenAssetsStatus(txid, vout, 3);
                if (tokenAssets != null)
                    tokenAssetsList.add(tokenAssets); // 状态不为3

            }

            if (tokenAssetsList != null && tokenAssetsList.size() > 0)
                return tokenAssetsList;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public BigInteger findToTokenByScript(List<String> script) {
        return scriptTokenLinkMapper.findToTokenByScript(script);
    }

    @Override
    public BigInteger findFromTokenByScript(List<String> script) {
        return scriptTokenLinkMapper.findFromTokenByScript(script);
    }

    @Override
    public BigInteger findDestructionByScript(List<String> script) {
        return scriptTokenLinkMapper.findDestructionByScript(script);
    }


}
