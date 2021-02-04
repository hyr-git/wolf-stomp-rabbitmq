package com.naah.redis.redpackage.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedPackageService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    
    @Resource
    private Redisson redisson;
	
	private String RED_PACKAGE_KEY = "red_package:lock";
	
	private String RED_PACKAGE_MAP = "redStcock";

	private String RED_PACKAGE_LOCK = "redPackageLock";
	
	public String grabRedPackage(Long redPacketId, Long userId) {
		//String.join(":", RED_PACKAGE_KEY,userId)
	  /*  40-60 10万
	    40以下30万*/
		
		String userToken = UUID.randomUUID().toString();
	    try {
	    	/*
	    	 * 为当前用户线程添加指定userToken的用户锁,同时指定锁的超时间为10秒
	    	 */
		    boolean flag = redisTemplate.opsForValue().setIfAbsent(RED_PACKAGE_LOCK, userToken,10,TimeUnit.SECONDS);

		    if(!flag) {
		    	return "当前人员多,请稍后再试";
		    }
		    
		    //获取当前库存信息
		    int stockNum = (int) redisTemplate.opsForValue().get(RED_PACKAGE_KEY);
		    
		    //扣减库存
		    if(stockNum>0){
		    	insertDataBase(userToken, stockNum+"");
		    	long num = redisTemplate.opsForValue().decrement(RED_PACKAGE_KEY);
		    	log.info("库存扣减成功之后,当前库存数量--->"+num);
		    	return "库存扣减成功之后,当前库存数量--->"+num;
		    }else {
		    	//insertDataBase(userToken, "库存不足库存扣减失败");
		    	log.info("库存不足库存扣减失败");
		    	return "库存不足库存扣减失败";
		    }
		}catch(Exception e) {
			e.printStackTrace();
		} 
	    finally {
	    	String token = (String) redisTemplate.opsForValue().get(RED_PACKAGE_LOCK);
	    	/*
	    	 * 校验当前的锁是否是当前用户线程进行加锁
	    	 * 并发抢购会导致前面的线程会释放后面线程加的锁
	    	 * 在某些特殊场景下：
	    	 *    若加锁的超时时间为10后自动释放锁,而改业务执行需要15秒,当第一个线程执行到第10秒时,redis会自动释放锁;
	    	 *    此时第二个线程加锁成功,执行到第5秒,即第一个线程就会执行finally方法释放锁,此时若没有进行当前token判断,
	    	 *    则第一个线程会释放第二个线程加的锁,此时会导致加锁无效
	    	 */
	    	if(userToken.equals(token)) {
	    		redisTemplate.delete(RED_PACKAGE_LOCK);
	    	}
		}
		return "抢购成功"; 
	}
	
	
	public void getRedPackage() {
		/*Set<Object> sets = redisTemplate.opsForHash().keys("redStcock");
		Iterator its = sets.iterator();
		while(its.hasNext()) {
			System.out.println(its.next());
		}*/
		
		List<Object> list = redisTemplate.opsForHash().values("redStcock");
		list.stream().sorted().forEach(s->System.out.println(s));
	}
	
	public String grabRedPackageRedison(Long redPacketId, Long userId) {
		//String.join(":", RED_PACKAGE_KEY,userId)
	  /*  40-60 10万
	    40以下30万*/
		redisTemplate.opsForValue().increment("getRed");
    	RLock lock = redisson.getLock(RED_PACKAGE_LOCK);
	    try {
	    	/*
	    	 * 为当前用户线程添加指定userToken的用户锁,同时指定锁的超时间为10秒
	    	 */
	    	boolean flag = lock.tryLock(15, TimeUnit.SECONDS);
		    if(!flag) {
		    	return "redisson--当前人员多,请稍后再试";
		    }
		    
		    //获取当前库存信息
		    int stockNum = (int) redisTemplate.opsForValue().get(RED_PACKAGE_KEY);
			String userToken = UUID.randomUUID().toString();

		    //扣减库存
		    if(stockNum>0){
		    	insertDataBase(userToken, stockNum+"");
		    	long num = redisTemplate.opsForValue().decrement(RED_PACKAGE_KEY);
		    	//抢购成
		    	log.info("redisson--库存扣减成功之后,当前库存数量--->"+num);
		    	return "redisson--库存扣减成功之后,当前库存数量--->"+num;
		    }else {
		    	//insertDataBase(userToken, "redisson--库存不足库存扣减失败");
		    	log.info("redisson--库存不足库存扣减失败");
		    	return "redisson--库存不足库存扣减失败";
		    }
		}catch(Exception e) {
			e.printStackTrace();
		} 
	    finally {
	    	if(lock.isLocked()) {
	    		if(lock.isHeldByCurrentThread()) {
	    			lock.unlock();
	    		}
	    	}
	    	
		}
		return "抢购成功"; 
	}
	
	private void insertDataBase(String token,String number) {
		redisTemplate.opsForHash().put(RED_PACKAGE_MAP, token, number);
	}
}
