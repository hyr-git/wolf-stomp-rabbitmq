package com.naah.redis.lock.redisson;

import javax.annotation.PostConstruct;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

/**
 * Created by fangzhipeng on 2017/4/5.
 * 获取RedissonClient连接类
 */
@Component
public class RedissonConnector {
	
    RedissonClient redisson;
    
    @PostConstruct
    public void init(){
    	Config config = new Config();
    	config.useSingleServer().setAddress("dev.carsir.xin:6379");
    	config.useSingleServer().setPassword("devTaG8ie0)fE");
        redisson = Redisson.create(config);
    }

    public RedissonClient getClient(){
        return redisson;
    }

}