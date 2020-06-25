package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.AddressScriptLink;

import java.util.List;

public interface AddressScriptLinkService {

    int insert(AddressScriptLink addressScriptLink);

    int findCount(String address, String script);

    List<String> findListByAddress(String address);

    List<String> findByScript(String script);

}
