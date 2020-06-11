package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.ScriptSlpMapper;
import com.upload.app.modular.system.model.ScriptSlp;
import com.upload.app.modular.system.service.ScriptSlpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ScriptSlpServiceImpl implements ScriptSlpService {

    @Resource
    private ScriptSlpMapper scriptSlpMapper;

    @Override
    public int insertSlp(ScriptSlp slp) {
        return scriptSlpMapper.insertSlp(slp);
    }

    @Override
    public ScriptSlp findByTokenId(String tokenId) {
        return scriptSlpMapper.findByTokenId(tokenId);
    }

    @Override
    public List<ScriptSlp> queryTokenInfoList(Map<String, Object> query) {
        return scriptSlpMapper.queryTokenInfoList(query);
    }

    @Override
    public Long queryTokenInfoCount(Map<String, Object> query) {
        return scriptSlpMapper.queryTokenInfoCount(query);
    }

}
