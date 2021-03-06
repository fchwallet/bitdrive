package com.upload.app.core.redis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class RedisBasedDistributedLock extends AbstractLock {

    private Jedis jedis;
    protected String lockKey;//锁的名字
    protected long lockExpire;//锁的有效时长(毫秒)

    public RedisBasedDistributedLock(Jedis jedis,String lockKey,long lockExpire){
        this.jedis = jedis;
        this.lockKey = lockKey;
        this.lockExpire = lockExpire;
    }


    @Override
    protected void unlock0() {
        // 判断锁是否过期
        String value = jedis.get(lockKey);
        if (!isTimeExpired(value)) {
            doUnlock();
        }
    }

    @Override
    protected boolean lock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt) throws InterruptedException {
        if (interrupt) {
            checkInterruption();
        }
        long start = System.currentTimeMillis();
        long timeout = unit.toMillis(time);//
        while (useTimeout ? isTimeout(start, timeout) : true) {
            if (interrupt) {
                checkInterruption();
            }
            long lockExpireTime = System.currentTimeMillis() + lockExpire + 1;//锁的超时时间
            String stringLockExpireTime = String.valueOf(lockExpireTime);
            if (jedis.setnx(lockKey, stringLockExpireTime) == 1) {//获取到锁
                //成功获取到锁，设置相关标识
                locked = true;
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean tryLock() {

        long lockExpireTime = System.currentTimeMillis() + lockExpire + 1;//锁超时时间
        String stringOfLockExpireTime = String.valueOf(lockExpireTime);
        if (jedis.setnx(lockKey, stringOfLockExpireTime) == 1) {//获取到锁
            //设置相关标识
            locked = true;
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        String value = jedis.get(lockKey);
        if (value != null && isTimeExpired(value)) {//锁是过期的
            //假设多个线程(非单jvm)同时走到这里
            String oldValue = jedis.getSet(lockKey, stringOfLockExpireTime);//原子操作
            // 但是走到这里时每个线程拿到的oldValue肯定不可能一样(因为getset是原子性的)
            // 假如拿到的oldValue依然是expired的，那么就说明拿到锁了
            if (oldValue != null && isTimeExpired(oldValue)) {//拿到锁
                //设置相关标识
                locked = true;
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
        }
        return false;

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    /**
     * 释放锁
     * @date 2017-10-18
     */
    public void doUnlock() {
        jedis.del(lockKey);
    }

    /**
     * 检测时间是否过期
     * @param value
     * @return
     * @date 2017-10-18
     */
    public boolean isTimeExpired(String value) {
        return Long.parseLong(value) < System.currentTimeMillis();
    }

    /**
     * 查询当前的锁是否被别的线程占有
     * @return 被占有true，未被占有false
     * @date 2017-10-18
     */
    public boolean isLocked(){
        if (locked) {
            return true;
        }else {
            String value = jedis.get(lockKey);
            // TODO 这里其实是有问题的, 想:当get方法返回value后, 假设这个value已经是过期的了,
            // 而就在这瞬间, 另一个节点set了value, 这时锁是被别的线程(节点持有), 而接下来的判断
            // 是检测不出这种情况的.不过这个问题应该不会导致其它的问题出现, 因为这个方法的目的本来就
            // 不是同步控制, 它只是一种锁状态的报告.
            return !isTimeExpired(value);
        }
    }

    /*
     * 判断是否超时(开始时间+锁等待超时时间是否大于系统当前时间)
     */
    public boolean isTimeout(long start, long timeout) {
        return start + timeout > System.currentTimeMillis();
    }

    /*
     * 检查当前线程是否阻塞
     */
    public void checkInterruption() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }


}
