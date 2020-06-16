package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.Create;

public interface CreateService {

    int insert(Create Create);

    Create findByDriveId(String driveId);

    int update(Create create);

}
