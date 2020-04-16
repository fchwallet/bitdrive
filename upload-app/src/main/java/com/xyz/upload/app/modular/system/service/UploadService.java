package com.xyz.upload.app.modular.system.service;

import com.xyz.upload.app.modular.system.model.Upload;

import java.util.List;

public interface UploadService {

    int insertUpload(Upload upload);

    List<Upload> findListByOpenId(Integer openId);

    Upload findByName(String txid, Integer openId);

}
