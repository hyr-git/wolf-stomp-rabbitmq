package com.naah.common.boolfilter.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/** 
 * redis操作布隆过滤器 
 * 
 * @param <T> 
 * @author xhj 
 */
public class RedisBloomFilter<T> {
	
	static Jedis redis = new Jedis("dev.carsir.xin", 6379, 50000);
	static {
		redis.auth("devTaG8ie0)fE");
	}
	//JedisUtils redisClient = new Jedis("dev.carsir.xin");
	
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 删除缓存的KEY
	 *
	 * @param key KEY
	 */
	public void delete(String key) {
		redis.del(key);
		//redisTemplate.delete(key);
	}

	/**
	 * 根据给定的布隆过滤器添加值，在添加一个元素的时候使用，批量添加的性能差
	 *
	 * @param bloomFilterHelper 布隆过滤器对象
	 * @param key  KEY
	 * @param value 值
	 * @param <T> 泛型，可以传入任何类型的value
	 */
	public <T> void add(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			//redisTemplate.opsForValue().setBit(key, i, true);
			redis.setbit(key, i, true);
		}
	}

	/**
	 * 根据给定的布隆过滤器添加值，在添加一批元素的时候使用，批量添加的性能好，使用pipeline方式(如果是集群下，请使用优化后RedisPipeline的操作)
	 *
	 * @param bloomFilterHelper 布隆过滤器对象
	 * @param key  KEY
	 * @param valueList 值，列表
	 * @param <T>   泛型，可以传入任何类型的value
	 */
	public <T> void addList(BloomFilterHelper<T> bloomFilterHelper, String key, List<T> valueList) {
		
		// 开启流水线
		Pipeline pipeline = redis.pipelined();
		for (T value : valueList) {
			int[] offset = bloomFilterHelper.murmurHashOffset(value);
			for (int i : offset) {
				pipeline.setbit(key, i, true);
			}
		}
		List result = pipeline.syncAndReturnAll();
		result.stream().forEach(s->System.out.println(">>>>>"+s));
		/*redisTemplate.executePipelined(new RedisCallback<Long>() {
		@Override
		public Long doInRedis(RedisConnection connection) throws DataAccessException {
			connection.openPipeline();
			for (T value : valueList) {
				int[] offset = bloomFilterHelper.murmurHashOffset(value);
				for (int i : offset) {
					connection.setBit(key.getBytes(), i, true);
				}
			}
			return null;
		} });*/
	}

	/**
	 * 根据给定的布隆过滤器判断值是否存在
	 *
	 * @param bloomFilterHelper 布隆过滤器对象
	 * @param key KEY
     * @param value 值
	 * @param <T>   泛型，可以传入任何类型的value
	 * @return 是否存在
	 */
	public <T> boolean contains(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			if(!redis.getbit(key, i)) {
				return false;
			}
			/*if (!redisTemplate.opsForValue().getBit(key, i)) {
				return false;
			}*/
		}
		return true;
	}
}