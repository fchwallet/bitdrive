package com.upload.app.modular.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.upload.app.modular.system.model.ScriptUtxoTokenLink;

public interface RedisQueueService {

    ScriptUtxoTokenLink blpop(String address);

    Long lpush(ScriptUtxoTokenLink scriptUtxoTokenLink) throws JsonProcessingException;

}
