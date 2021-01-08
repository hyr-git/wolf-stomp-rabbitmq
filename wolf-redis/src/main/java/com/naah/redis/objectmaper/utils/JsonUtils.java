package com.naah.redis.objectmaper.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 
/**
 * @Date 2019/1/7 11:10
 * @Description
 */
public class JsonUtils {
 
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
    
    
    /**
	 * 实体对象转换成Json字符串
	 * @param t 实体对象T
	 * @return T
	 */
	public  static <T> String objectToJson(T t){
		try {
			return objectMapper.writeValueAsString(t);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 * Json字符串转换成实体对象
	 * @param json
	 * @param clazz 实体对象所属类Class
	 * @return
	 */
	public  static <T> T jsonToObject(String json, Class<T> clazz){
		try {
			return objectMapper.readValue(json, clazz);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 * Json字符串转换成List
	 * @param json
	 * @param clazz 实体对象所属类Class
	 * @return
	 */
	public  static <T> T jsonToList(String json, Class<T> clazz){
		try {
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
			return objectMapper.readValue(json, javaType);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 * Json字符串转换成List
	 * @param json
	 * @param typeReference TypeReference<T>
	 * <p>
	 * 	<pre>new TypeReference<List<User>>() {}</pre>
	 * </p>
	 * @return
	 */
	public static <T> T jsonToList(String json, TypeReference<T> typeReference){
		try {
			return objectMapper.readValue(json, typeReference);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 *  美化输出
	 * @param t
	 * @return
	 */
	public  static <T> String console(T t){
		String json = "";
		try {
			json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
 
    
 
    /**
     * 对象转为字符串
     *
     * @param obj
     * @return
     */
    public static String Object2Json(Object obj) {
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return jsonStr;
    }
 
    /**
     * 对象转为byte数组
     *
     * @param obj
     * @return
     */
    public static byte[] object2ByteArray(Object obj) {
        byte[] byteArr = new byte[0];
        try {
            byteArr = objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return byteArr;
    }
 
    /**
     * json字符串转为对象
     *
     * @param jsonStr
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> T json2Object(String jsonStr, Class<T> beanType) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonStr, beanType);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return t;
    }
 
    /**
     * byte数组转为对象
     *
     * @param byteArr
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> T byteArr2Object(byte[] byteArr, Class<T> beanType) {
        T t = null;
        try {
            t = objectMapper.readValue(byteArr, beanType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
 
    /**
     * 集合转为字符串
     * @param list
     * @return
     */
    public static String list2String(List<?> list) {
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return jsonStr;
    }
 
    /**
     * 字符串转集合
     *
     * @param jsonStr
     * @return
     */
    public static List<?> json2List(String jsonStr) {
        List<?> list = null;
        try {
            list = objectMapper.readValue(jsonStr, List.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return list;
    }
 
    /**
     * Map转为字符串
     *
     * @param map
     * @return
     */
    public static String map2String(Map<?, ?> map) {
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return jsonStr;
    }
 
    /**
     * 字符串转Map
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> json2Map(String jsonStr) {
        Map<?, ?> map = null;
        try {
            map = objectMapper.readValue(jsonStr, Map.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return map;
    }
 
}