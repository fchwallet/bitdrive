package com.xyz.upload.app.modular.system.dao;

import com.xyz.upload.app.modular.system.model.Upload;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UploadMapper {

    int insertUpload(Upload upload);

    List<Upload> findListByOpenId(Integer openId);

    Upload findByName(@Param("txid") String txid, @Param("openId") Integer openId);

}
