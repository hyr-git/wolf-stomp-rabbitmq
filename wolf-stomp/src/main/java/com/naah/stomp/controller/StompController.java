package com.naah.stomp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class StompController {

    private static final Logger logger=LoggerFactory.getLogger(StompController.class);
    
    //使用MessageMapping注解来标识所有发送到"/chat"这个destination的消息,都会被路由到这个方法进行处理
    @MessageMapping("/chat1")
    //使用SendTo注解来标识这个方法返回的结果,都会被发送到指定的destination
    //SendTo 发送至 Broker 下的指定订阅路径
    //传入的参数为客户端发送过来的消息,是自动绑定的
    @SendTo("/toAll/bulletScreen")
    public String say(com.naah.common.model.BulletMessageDTO clientMessage) {
        //方法用于广播测试
        if (clientMessage!=null){
            if (clientMessage.getMessage()!=null){
                clientMessage.setMessage(clientMessage.getMessage().trim());
            }
        }
        logger.info(clientMessage.getUsername()+":"+clientMessage.getMessage());
        return clientMessage.getMessage();
    }

    //注入SimpMessagingTemplate 用于点对点消息发送
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

}