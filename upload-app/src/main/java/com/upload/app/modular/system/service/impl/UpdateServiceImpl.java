package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.UpdateMapper;
import com.upload.app.modular.system.model.Update;
import com.upload.app.modular.system.service.UpdateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Resource
    private UpdateMapper updateMapper;

    @Override
    public Update findByDriveId(String driveId, String updateId) {
        return updateMapper.findByDriveId(driveId, updateId);
    }

    @Override
    public int insert(Update update) {
        return updateMapper.insert(update);
    }

    @Override
    public Update findByDriveIdLimt(String driveId) {
        return updateMapper.findByDriveIdLimt(driveId);
    }

}
