package com.xyz.upload.app.modular.system.service;

import com.xyz.upload.app.modular.system.model.FchXsvLink;

public interface FchXsvLinkService {

    int insert(FchXsvLink fchXsvLink);

    FchXsvLink findByXsv(String xsvAddress);

    FchXsvLink findByFch(String fchAddress);

    FchXsvLink findByHash(String addressHash);

}
