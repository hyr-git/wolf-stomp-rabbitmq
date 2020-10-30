package com.naah.stomp.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.naah.stomp.config.RabbitConfig;
import com.naah.stomp.jiguang.utils.JPushManage;
import com.naah.stomp.model.MQMessage;


/**
 * @Auther: zj
 * @Date: 2019/4/17 12:53
 * @Description: 消息消费者
 */
@Component
public class Receiver {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    

   /* @Autowired
    RecordService recordService;*/

    @RabbitHandler
    @RabbitListener(queues = RabbitConfig.TO_WEB_QUEUE_NAME)
    public void processToWeb(@Payload MQMessage message,@Headers Map<String,Object> header ) throws IOException {
        System.out.println("processToWeb  : " + JSON.toJSONString(message));
    	String toUserId = message.getToUserId();
    	toUserId = "16033333333";
        messagingTemplate.convertAndSend( "/"+RabbitConfig.TO_WEB_QUEUE_NAME+"/"+toUserId, message);
      	/***
      	 * 
      	 * /user/16033333333/toWeb
      	 *messagingTemplate.convertAndSendToUser(toUserId,  "/"+RabbitConfig.TO_WEB_QUEUE_NAME, message);
      	 **/   
    }
    
    /***
     * 考虑直接使用极光推送
     * @param context
     * @throws IOException
     */
    @RabbitHandler
    @RabbitListener(queues = RabbitConfig.TO_APP_QUEUE_NAME)
    public void processToApp(@Payload MQMessage message,@Headers Map<String,Object> header ) throws IOException {
       System.out.println("processToAPP  : " + JSON.toJSONString(message));
       String toUserId = message.getToUserId();
       // amqpTemplate.convertAndSend( destination, context);
        messagingTemplate.convertAndSend( "/"+RabbitConfig.TO_APP_QUEUE_NAME, message);
        List<String> tagsList = new ArrayList<>();
    	tagsList.add(toUserId);
    	tagsList.add("17088888888");
        JPushManage.sendToAliasList(tagsList,JSON.toJSONString(message));
    }

}
