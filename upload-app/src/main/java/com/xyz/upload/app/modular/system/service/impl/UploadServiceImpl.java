package com.xyz.upload.app.modular.system.service.impl;

import com.xyz.upload.app.modular.system.dao.UploadMapper;
import com.xyz.upload.app.modular.system.model.Upload;
import com.xyz.upload.app.modular.system.service.UploadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UploadServiceImpl implements UploadService {

    @Resource
    private UploadMapper uploadMapper;

    @Override
    public int insertUpload(Upload upload) {
        return uploadMapper.insertUpload(upload);
    }

    @Override
    public List<Upload> findListByOpenId(Integer openId) {
        return uploadMapper.findListByOpenId(openId);
    }

    @Override
    public Upload findByName(String txid, Integer openId) {
        return uploadMapper.findByName(txid, openId);
    }
}
