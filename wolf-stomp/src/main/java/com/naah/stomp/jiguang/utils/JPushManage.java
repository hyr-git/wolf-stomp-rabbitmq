package com.naah.stomp.jiguang.utils;
import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Component
public class JPushManage {
    public static final String pushPath = "xyaq";

    //在极光注册上传应用的 appKey 和 masterSecret
    public static final String appKey = "1e623dc9152d6b4f7d465aa7";//必填，例如466f7032ac604e02fb7bda89
    public static final String masterSecret = "091d24d2a8955c1b897e8dd6";//必填，每个应用都对应一个masterSecret

    
    public Integer sendPush(List<String> aliasList){
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, ClientConfig.getInstance());
        return JPushUtil.sendToAliasList(jpushClient,aliasList,"这是一个IOS推送测试","extra_key","extra_value","测试");
    }
    
    public static void main(String[] args) {
    	
    	
    	JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, ClientConfig.getInstance());
    	for (int i = 1; i <= 10; i++) {
    		tewst(jpushClient,i);
		}
	}
    
    private static void tewst(JPushClient jpushClient,int i) {

    	List<String> tagsList = new ArrayList<>();
    	tagsList.add("16033333333");
    	tagsList.add("17088888888");
    	JSONObject json = new JSONObject();
    	json.put("userId", UUID.randomUUID());
    	json.put("workType", "checkOrder");
    	json.put("url", "dev.carsir.xin");
    	//JPushUtil.sendToTagList(jpushClient,tagsList,"这是一个IOS推送测试",json.toJSONString(),"https://www.baidu.com","测试");
    	JPushUtil.sendToAliasList(jpushClient,tagsList,"这是一个IOS推送测试"+i,json.toJSONString(),"https://www.baidu.com","测试"+UUID.randomUUID());
    	
    }
}