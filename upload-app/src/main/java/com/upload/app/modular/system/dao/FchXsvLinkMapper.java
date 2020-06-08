package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.FchXsvLink;

import java.util.List;

public interface FchXsvLinkMapper {

    int insert(FchXsvLink fchXsvLink);

    FchXsvLink findByXsv(String xsvAddress);

    FchXsvLink findByFch(String fchAddress);

    FchXsvLink findByHash(String addressHash);

    List<FchXsvLink> findByType(Integer type);

}
