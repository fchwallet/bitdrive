package com.xyz.upload.app.modular.system.dao;

import com.xyz.upload.app.modular.system.model.FchXsvLink;

public interface FchXsvLinkMapper {

    int insert(FchXsvLink fchXsvLink);

    FchXsvLink findByXsv(String xsvAddress);

    FchXsvLink findByFch(String fchAddress);

    FchXsvLink findByHash(String addressHash);

}
