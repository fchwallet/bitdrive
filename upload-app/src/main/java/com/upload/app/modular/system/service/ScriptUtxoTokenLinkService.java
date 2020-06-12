package com.upload.app.modular.system.service;


import com.upload.app.modular.system.model.ScriptUtxoTokenLink;

import java.util.List;

public interface ScriptUtxoTokenLinkService {

    int insert(ScriptUtxoTokenLink scriptUtxoTokenLink);

    int deleteUtxoToken(String txid, Integer n);

    List<ScriptUtxoTokenLink> findListByScript(List<String> script, String addressHash);

}
