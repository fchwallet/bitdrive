package com.xyz.upload.app.modular.system.service;

import com.xyz.upload.app.modular.system.model.Update;

import java.util.List;

public interface UpdateService {

    List<Update> findByDriveId(String driveId);

    int insert(Update update);

    Update findByDriveIdLimt(String driveId);

}
