package com.naah.redis.objectmaper;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.naah.redis.objectmaper.vo.Product;

public class ObjectMaperDemo {
	
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	 static {
	        // 转换为格式化的json
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
	    	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        //修改日期格式
	    	objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	    	
	    	//设置实体无属性和json串属性对应时不会出错
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);//设置在反序列化时忽略在JSON字符串中存在，而在Java中不存在的属性
	        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
	        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);//属性为null的转换
	        
			//转换为格式化的json
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			// 如果json中有新增的字段并且是实体类类中不存在的，不报错 
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			//如果是空对象的时候,不抛异常
	        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

			  //设置日期格式
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			
			//设置可用单引号
			objectMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);

			//设置字段可以不用双引号包括
			objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

	    }
	    

	public static void main(String[] args) throws IOException {
	
		String json = "{\"id\": \"0001\", \"name\" : \"Coca Cola\", \"price\": 3,\"length\":2}";
		Product product = objectMapper.readValue(json, Product.class);
		System.out.println(product);
		
		//对象转为字符串
		String  value = objectMapper.writeValueAsString(product);
		System.out.println("对象转为字符串:"+value);
		
		//对象转byte数组
		byte[]  valueByte = objectMapper.writeValueAsBytes(product);
	    System.out.println("对象转为字符串:"+valueByte);
	    
	    product = objectMapper.readValue(value, Product.class);
		System.out.println("字符串---对象:"+product);
		
	    product = objectMapper.readValue(valueByte, Product.class);
	    System.out.println("valueByte串---对象:"+product);
		
	    
	    String jsonCarArray = "[{\"id\": \"0001\", \"name\" : \"Coca Cola\", \"price\": 3}]";

	    // product.getId();   0001
		// 直接读取文件里面的值：
		//Product product = objectMapper.readValue(new File("target/json_car.json"), Product.class);
		
		Product product1 = new Product("0001", "name", 1);
		//objectMapper.writeValue(new File("target/product.json"), product);
		// 写入文件
	}
}
