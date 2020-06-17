package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.CreateMapper;
import com.upload.app.modular.system.service.CreateService;
import com.upload.app.modular.system.model.Create;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CreateServiceImpl implements CreateService {

    @Resource
    private CreateMapper createMapper;

    @Override
    public int insert(Create create) {
        return createMapper.insert(create);
    }

    @Override
    public Create findByDriveId(String driveId) {
        return createMapper.findByDriveId(driveId);
    }

    @Override
    public int update(Create create) {
        return createMapper.update(create);
    }

    @Override
    public Create findByData(String data) {
        return createMapper.findByData(data);
    }

}
