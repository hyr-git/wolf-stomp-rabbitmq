package com.naah.stomp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.naah.stomp.model.MQMessage;

@Controller
public class GreetingController {

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;
  
  /**
   * @MessageMapping用于向/app/greeting发送消息的路由
   * @SendTo用于将响应结果发送到对应的topic[toApp]中，客户端监听了此topic[toApp]的客户端可以收到消息
   * @param greeting
   * @return
   */
  @MessageMapping("/greeting")
  @SendTo("/toApp")
  public MQMessage handle(MQMessage greeting) {
    System.out.println("send:" + greeting);
    return greeting;            
  }

  /**
   * 一般用于初始化，直接订阅/app/init，如果成功，则可以直接得到一条响应消息，不用等到topic中有数据才能获得响应
   * @return
   */
  @SubscribeMapping("/init")
  public String sub(){
      return "how old are you ?";
  }

    // 用于定时向topic发送消息
  @Scheduled(cron = "*/5 * * * * *")
  public void sendMessage(){
    System.out.println("current time is :"  + System.currentTimeMillis());
    simpMessagingTemplate.convertAndSend("/topic/hello", "current time is :" + System.currentTimeMillis());
  }
}