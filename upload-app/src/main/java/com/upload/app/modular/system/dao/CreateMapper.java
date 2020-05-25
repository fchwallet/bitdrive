package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.Create;

public interface CreateMapper {

    int insert(Create Create);

    Create findByDriveId(String driveId);

}
