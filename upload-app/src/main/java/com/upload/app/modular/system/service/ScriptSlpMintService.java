package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.ScriptSlpMint;

public interface ScriptSlpMintService {

    int insertSlpMint(ScriptSlpMint slpMint);

    ScriptSlpMint findByToken(String tokenId, String address);

}
