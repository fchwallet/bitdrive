package com.xyz.upload.app.modular.system.service.impl;

import com.xyz.upload.app.modular.system.dao.FchXsvLinkMapper;
import com.xyz.upload.app.modular.system.model.FchXsvLink;
import com.xyz.upload.app.modular.system.service.FchXsvLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FchXsvLinkServiceImpl implements FchXsvLinkService {

    @Resource
    private FchXsvLinkMapper fchXsvLinkMapper;

    @Override
    public int insert(FchXsvLink fchXsvLink) {
        return fchXsvLinkMapper.insert(fchXsvLink);
    }

    @Override
    public FchXsvLink findByXsv(String xsvAddress) {
        return fchXsvLinkMapper.findByXsv(xsvAddress);
    }

    @Override
    public FchXsvLink findByFch(String fchAddress) {
        return fchXsvLinkMapper.findByFch(fchAddress);
    }

    @Override
    public FchXsvLink findByHash(String addressHash) {
        return fchXsvLinkMapper.findByHash(addressHash);
    }


}
