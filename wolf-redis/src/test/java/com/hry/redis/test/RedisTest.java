package com.hry.redis.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.naah.redis.RedisApplication;
import com.naah.redis.base.entiy.Shop;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=RedisApplication.class)
public class RedisTest{
	
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    
    
   /* @Autowired
    private Shop shop;*/
    
   /* @Autowired
    private List<Shop> shopList ;
    */
    //@Before(value = "")
    public void before(){
    	/*shop = new Shop();
    	shop.setId(UUID.randomUUID().toString());
    	shop.setShopName("店铺名称");
    	shop.setShopNo("S001");
    	shop.setShopAddress("店铺具体地址:河北省唐山市路南区");*/
    	
       /* shopList = new ArrayList<Shop>();
    	for (int i = 1; i <=10; i++) {
    		Shop shop = new Shop();
        	shop.setId(UUID.randomUUID().toString());
        	shop.setShopName("店铺名称"+i);
        	shop.setShopNo("S001"+i);
        	shop.setShopAddress("店铺具体地址:河北省唐山市路南区"+i+"号");
        	shopList.add(shop);
		}*/
    }
    

    @Test
    public void setShop(){
    	
    	/*注意：这里继续使用@AutoWired会报错，需要使用@Resource，这两个注解的区别在前者是根据类型后者
    	是根据名字，具体区别自行查资料（这里说一下为什么会报错：@AutoWired找不到该类型
    	<String,Object>的Bean因为根本没有。使用@Resource直接注入的是RedisTemplate）*/
    	
    	Shop shop = new Shop();
    	shop.setId(UUID.randomUUID().toString());
    	shop.setShopName("店铺名称");
    	shop.setShopNo("S001");
    	shop.setShopAddress("店铺具体地址:河北省唐山市路南区");
    	
    	redisTemplate.setKeySerializer(new StringRedisSerializer());
    	redisTemplate.setValueSerializer(RedisSerializer.json());
    	String key = String.join(":",  "shop","json",shop.getId());
    	redisTemplate.opsForValue().set(key,shop);
    	shop = (Shop) redisTemplate.opsForValue().get(key);
        System.out.println(key+">>>>"+shop);
    	
    	/*redisTemplate.setValueSerializer(RedisSerializer.byteArray());
    	key = String.join(":",  "shop","byteArray",shop.getId());
    	redisTemplate.opsForValue().set(key,shop);
    	shop = (Shop) redisTemplate.opsForValue().get(key);
        System.out.println(key+">>>>"+shop);
        */

    	redisTemplate.setValueSerializer(RedisSerializer.java());
    	key = String.join(":",  "shop","java",shop.getId());
    	redisTemplate.opsForValue().set(key,shop);
    	shop = (Shop) redisTemplate.opsForValue().get(key);
        System.out.println(key+">>>>"+shop);
        

    	redisTemplate.setValueSerializer(new StringRedisSerializer());
    	key = String.join(":",  "shop","string",shop.getId());
    	redisTemplate.opsForValue().set(key,JSON.toJSONString(shop));
    	Object sh =  redisTemplate.opsForValue().get(key);
        System.out.println(key+">>>>"+sh);

    }
    
    @Test
    public void setShopList(){
    	
    	List<Shop> shopList = new ArrayList<Shop>();
     	for (int i = 1; i <=10; i++) {
     		Shop shop = new Shop();
         	shop.setId(UUID.randomUUID().toString());
         	shop.setShopName("店铺名称"+i);
         	shop.setShopNo("S001"+i);
         	shop.setShopAddress("店铺具体地址:河北省唐山市路南区"+i+"号");
         	shopList.add(shop);
 		}
     	
     	redisTemplate.setKeySerializer(new StringRedisSerializer());
    	redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
    	
    	String key = "shop:list";
    	key = String.join(":",  "shop","list");
    	redisTemplate.opsForValue().set(key,JSON.toJSON(shopList));
    	
        shopList = (List<Shop>) redisTemplate.opsForValue().get(key);
        System.out.println(shopList);
    }
    
}