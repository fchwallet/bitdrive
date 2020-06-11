package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.ScriptSlpMintMapper;
import com.upload.app.modular.system.model.ScriptSlpMint;
import com.upload.app.modular.system.service.ScriptSlpMintService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScriptSlpMintServiceImpl implements ScriptSlpMintService {

    @Resource
    private ScriptSlpMintMapper scriptSlpMintMapper;

    @Override
    public int insertSlpMint(ScriptSlpMint slpMint) {
        return scriptSlpMintMapper.insertSlpMint(slpMint);
    }

    @Override
    public ScriptSlpMint findByToken(String tokenId, String address) {
        return scriptSlpMintMapper.findByToken(tokenId, address);
    }

}
