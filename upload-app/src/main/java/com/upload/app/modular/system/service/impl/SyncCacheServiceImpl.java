package com.upload.app.modular.system.service.impl;


import com.upload.app.core.util.JedisUtils;
import com.upload.app.modular.system.service.ISyncCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncCacheServiceImpl implements ISyncCacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncCacheServiceImpl.class);

    @Autowired
    private JedisUtils jedisUtils;

    /**
     * 获取redis中key的锁，乐观锁实现
     * @param lockName
     * @param expireTime 锁的失效时间
     * @return
     */
    @Override
    public Boolean getLock(String lockName, int expireTime) {
        return jedisUtils.lock(lockName);
    }

    /**
     * 释放锁，直接删除key(直接删除会导致任务重复执行，所以释放锁机制设为超时30s)
     * @param lockName
     * @return
     */
    @Override
    public void releaseLock(String lockName) {
        jedisUtils.delete(lockName);
    }

}
