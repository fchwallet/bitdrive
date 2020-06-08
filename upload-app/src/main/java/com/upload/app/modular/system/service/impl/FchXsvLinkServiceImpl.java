package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.FchXsvLinkMapper;
import com.upload.app.modular.system.model.FchXsvLink;
import com.upload.app.modular.system.service.FchXsvLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public List<FchXsvLink> findByType(Integer type) {
        return fchXsvLinkMapper.findByType(type);
    }


}
