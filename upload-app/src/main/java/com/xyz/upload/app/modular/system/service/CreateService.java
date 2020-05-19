package com.xyz.upload.app.modular.system.service;

import com.xyz.upload.app.modular.system.model.Create;

public interface CreateService {

    int insert(Create Create);

    Create findByDriveId(String driveId);

}
