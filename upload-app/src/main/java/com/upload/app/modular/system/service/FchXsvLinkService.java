package com.upload.app.modular.system.service;

import com.alibaba.fastjson.JSONObject;
import com.upload.app.modular.system.model.FchXsvLink;

import java.util.List;

public interface FchXsvLinkService {

    int insert(FchXsvLink fchXsvLink);

    FchXsvLink findByXsv(String xsvAddress);

    FchXsvLink findByFch(String fchAddress);

    FchXsvLink findByHash(String addressHash);

    List<FchXsvLink> findByType(Integer type);

}
