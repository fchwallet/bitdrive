package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.Update;
import org.apache.ibatis.annotations.Param;


public interface UpdateMapper {

    Update findByDriveId(@Param("driveId") String driveId, @Param("updateId") String updateId);

    int insert(Update update);

    Update findByDriveIdLimt(String driveId);

}
