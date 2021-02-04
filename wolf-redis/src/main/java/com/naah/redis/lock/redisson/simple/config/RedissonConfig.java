package com.naah.redis.lock.redisson.simple.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson 配置类
 * Created on 2018/6/19 
 * 单机版与哨兵版本不能共存   RedissonClient  必须是单例模式
 * Field redissonClient in com.naah.redis.lock.redisson.pro.test.controller.LockController required a single bean, but 2 were found:
	- redissonSentinel: defined by method 'redissonSentinel' in class path resource [com/naah/redis/lock/redisson/pro/config/RedissonAutoConfiguration.class]
	- getRedisson: defined by method 'getRedisson' in class path resource [com/naah/redis/lock/redisson/simple/config/RedissonConfig.class]

 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;
    
    @Value("${spring.redis.database}")
    private int database;

    @Bean
    public RedissonClient getRedisson(){

        Config config = new Config();
        //config.useSingleServer().setAddress(host + ":" + port).setPassword(password).setDatabase(database);
        //config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password).setDatabase(database);
       
        //添加主从配置
        //config.useMasterSlaveServers().setMasterAddress("").setPassword("").addSlaveAddress(new String[]{"",""});

        //哨兵
        config.useSentinelServers().addSentinelAddress("redis://127.0.0.1:26379","redis://127.0.0.1:26380","redis://127.0.0.1:26381")
        .setMasterName("master").setPassword("").setDatabase(0);
        return Redisson.create(config);
    }

}