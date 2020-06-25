package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.SystemUtxo;

public interface BlockingQueueService {

    void addQueue(SystemUtxo systemUtxo);

    SystemUtxo take() throws InterruptedException;

}
