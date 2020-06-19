package com.upload.app.modular.system.service.impl;


import com.upload.app.modular.system.dao.ScriptUtxoTokenLinkMapper;
import com.upload.app.modular.system.model.ScriptUtxoTokenLink;
import com.upload.app.modular.system.service.ScriptUtxoTokenLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScriptUtxoTokenLinkServiceImpl implements ScriptUtxoTokenLinkService {

    @Resource
    private ScriptUtxoTokenLinkMapper scriptUtxoTokenLinkMapper;

    @Override
    public int insert(ScriptUtxoTokenLink scriptUtxoTokenLink) {
        return scriptUtxoTokenLinkMapper.insert(scriptUtxoTokenLink);
    }

    @Override
    public int deleteUtxoToken(String txid, Integer n) {
        return scriptUtxoTokenLinkMapper.deleteUtxoToken(txid, n);
    }

    @Override
    public List<ScriptUtxoTokenLink> findListByScript(List<String> script, String addressHash, String tokenId) {
        return scriptUtxoTokenLinkMapper.findListByScript(script, addressHash, tokenId);
    }

}
