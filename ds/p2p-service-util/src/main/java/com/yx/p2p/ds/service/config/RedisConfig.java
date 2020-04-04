package com.yx.p2p.ds.service.config;

import com.yx.p2p.ds.service.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.cache.host:disabled}")
    private String cacheHost;

    @Value("${spring.redis.cache.port:0}")
    private int cachePort;

    @Value("${spring.redis.cache.database:0}")
    private int cacheDatabase;

    @Bean
    public RedisUtil getRedisUtil(){
       if(cacheHost.equals("disabled")){
           return null;
       }

       RedisUtil redisUtil = new RedisUtil();
       redisUtil.initCachePool(cacheHost,cachePort,cacheDatabase);
       return redisUtil;
    }
}
