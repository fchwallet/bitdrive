package com.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.upload.app.core.util.JedisUtils;
import com.upload.app.modular.system.model.ScriptUtxoTokenLink;
import com.upload.app.modular.system.service.RedisQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisQueueServiceImpl implements RedisQueueService {

    @Autowired
    private JedisUtils jedisUtils;

    @Override
    public ScriptUtxoTokenLink blpop(String address) {
        String json = (String)jedisUtils.blpop(address);
        return  JSONObject.parseObject(json, ScriptUtxoTokenLink.class);
    }

    @Override
    public Long lpush(ScriptUtxoTokenLink scriptUtxoTokenLink) throws JsonProcessingException {
        return jedisUtils.lpush(scriptUtxoTokenLink.getAddress(), scriptUtxoTokenLink);
    }
}
