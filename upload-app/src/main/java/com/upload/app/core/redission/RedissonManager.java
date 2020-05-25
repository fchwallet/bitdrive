package com.upload.app.core.redission;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

public class RedissonManager {
    private static final String RAtomicName = "genId_";

    private Config config = new Config();
    private RedissonClient redisson = null;

//    @Value("${redis.cache.expiration:3600}")
//    private Long expiration;
//    @Value("${redis.host}")
//    private String host;
//    @Value("${redis.port}")
//    private int port;
//    @Value("${redis.password}")
//    private String password;
//    @Value("${redis.database}")
//    private int database;

    public void init(String host,int port,String password,int database){
        try {
            SingleServerConfig ssc = config.useSingleServer()
                    .setAddress("redis://"+host+":"+port)
                    .setDatabase(database);
            if(StringUtils.isNotBlank(password))
                ssc.setPassword(password);

            redisson = Redisson.create(config);
//            //清空自增的ID数字
//            RAtomicLong atomicLong = redisson.getAtomicLong(RAtomicName);
//            atomicLong.set(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RedissonClient getRedisson(){
        return redisson;
    }

    /** 获取redis中的原子ID */
    public Long nextID(){
        RAtomicLong atomicLong = getRedisson().getAtomicLong(RAtomicName);
        atomicLong.incrementAndGet();
        return atomicLong.get();
    }
}
