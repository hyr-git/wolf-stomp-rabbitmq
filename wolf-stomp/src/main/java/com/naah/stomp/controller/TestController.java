package com.naah.stomp.controller;
import java.sql.SQLException;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.naah.stomp.model.User;


@Controller
public class TestController {
    
    
    @RequestMapping(value="/sockjs.do")
    public String test0(Model model){
        return "sockjs";
    }    
    
    @RequestMapping(value="/stompjs.do")
    public String test1(Model model){
        return "stomp";
    }    
    
    /**
     * 表示该方法处理客户端发来的/td/stomp1.do或者/app/stomp1.do。
     * sendTo:重新指定发送的位置，默认原路返回（url会加上/topic前缀）
     * 需走代理
     */
    @MessageMapping("/stomp1.do")
    @SendTo("/topic/hello")
    public User handleStomp(User user) {
        System.out.println("stomp接收到客户端的请求："+user);
        user.setName("messagemapping返回user");
        return user;
        
    }
    
    /**
     * 用于处理messagemapping抛出的异常，类比exceptionhandler
     * @return
     */
    @MessageExceptionHandler({Exception.class,SQLException.class})
    @SendTo("/topic/errorTopic")
    public User errorHandler(Throwable t) {
        System.out.println("异常统一处理");
        User user = new User();
        user.setName("异常统一处理:"+t.getMessage());;
        return user;
    }
    
    
    /**
     * 触发方式和messagemapping一致。
     * sendTo:重新指定发送的位置，默认原路返回（url会加上/topic前缀）
     * 使用subscribemapping不走代理
     */
    @SubscribeMapping("/stomp2.do")
    @SendTo("/topic/hello")
    public User subsTest() {
        User user2 = new User();
        user2.setName("订阅name");
        user2.setPhone("subscribePhone");
        return user2;
    }
    
    
}