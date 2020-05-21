package com.xyz.upload.app.modular.system.dao;

import com.xyz.upload.app.modular.system.model.Update;

import java.util.List;

public interface UpdateMapper {

    List<Update> findByDriveId(String driveId);

    int insert(Update update);

    Update findByDriveIdLimt(String driveId);

}
