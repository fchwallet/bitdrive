package com.xyz.upload.app.core.redission;

import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

@Component
public class DistributedRedisLock {
    @Autowired
    private RedissonManager redissonManager;
    //private static RedissonClient redisson = RedissonManager.getRedisson();
    private static final String LOCK_TITLE = "redisLock:";


    public void acquire(String lockName){
        String key = LOCK_TITLE + lockName;
        RLock mylock = redissonManager.getRedisson().getLock(key);
        mylock.lock(60, TimeUnit.SECONDS); //lock提供带timeout参数，timeout结束强制解锁，防止死锁
    }

    public void release(String lockName){
        String key = LOCK_TITLE + lockName;
        RLock mylock = redissonManager.getRedisson().getLock(key);
        mylock.unlock();
    }

    /**
     * 尝试获取锁
     * @param lockName
     * @return
     */
    public boolean tryAcquire(String lockName,long leaseTimeSeconds){
        String key = LOCK_TITLE + lockName;
        RLock mylock = redissonManager.getRedisson().getLock(key);
        boolean flag =false;
        try {
            flag = mylock.tryLock(1,leaseTimeSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
