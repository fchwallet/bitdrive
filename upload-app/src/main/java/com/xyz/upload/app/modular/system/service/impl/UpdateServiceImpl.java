package com.xyz.upload.app.modular.system.service.impl;

import com.xyz.upload.app.modular.system.dao.UpdateMapper;
import com.xyz.upload.app.modular.system.model.Update;
import com.xyz.upload.app.modular.system.service.UpdateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Resource
    private UpdateMapper updateMapper;

    @Override
    public List<Update> findByDriveId(String driveId) {
        return updateMapper.findByDriveId(driveId);
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
