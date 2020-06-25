package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.BlockingQueueService;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

@Service
public class BlockingQueueServiceImpl implements BlockingQueueService {

    private static LinkedBlockingQueue<SystemUtxo> concurrentLinkedQueue = new LinkedBlockingQueue<SystemUtxo>();

    @Override
    public void addQueue(SystemUtxo systemUtxo) {
        concurrentLinkedQueue.add(systemUtxo);
    }

    @Override
    public SystemUtxo take() throws InterruptedException {
        return concurrentLinkedQueue.take();
    }

}
