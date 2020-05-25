package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.Update;


public interface UpdateService {

    Update findByDriveId(String driveId, String updateId);

    int insert(Update update);

    Update findByDriveIdLimt(String driveId);

}
