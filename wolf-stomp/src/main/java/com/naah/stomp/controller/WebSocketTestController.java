package com.naah.stomp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.naah.stomp.model.RequestMessage;
import com.naah.stomp.model.ResponseMessage;

/***
 * 使用@MessageMapping注解来标识所有发送到"/chat"这个destination的消息,都会被路由到这个方法进行处理
 * 使用@SendTo注解来标识这个方法的返回值,都会被发送到他指定的destination,"/topic"
 * 传入的参数RequestMessage reqestMessage为客户端发送过来的消息,是自动绑定的
 * @author m1832
 *
 */
@RestController
public class WebSocketTestController {
 
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
 
    /**聊天室（单聊+多聊）
     * @CrossOrigin 跨域
     * @MessageMapping 注解的方法可以使用下列参数:
     * * 使用@Payload方法参数用于获取消息中的payload（即消息的内容）
     * * 使用@Header 方法参数用于获取特定的头部
     * * 使用@Headers方法参数用于获取所有的头部存放到一个map中
     * * java.security.Principal 方法参数用于获取在websocket握手阶段使用的用户信息
     * @param requestMessage
     * @throws Exception
     */
    @CrossOrigin
    @MessageMapping("/chat")
    public void messageHandling(RequestMessage requestMessage) throws Exception {
        String destination = "/topic/" + HtmlUtils.htmlEscape(requestMessage.getRoom());
 
        String sender = HtmlUtils.htmlEscape(requestMessage.getSender());  //htmlEscape  转换为HTML转义字符表示
        String type = HtmlUtils.htmlEscape(requestMessage.getType());
        String content = HtmlUtils.htmlEscape(requestMessage.getContent());
        ResponseMessage response = new ResponseMessage(sender, type, content);
 
        messagingTemplate.convertAndSend(destination, response);
    }
 
    /**
     * 群发消息
     * @param requestMessage
     * @return
     * @throws Exception
     */
    @CrossOrigin
    @MessageMapping("/chatAll")
   public void messageHandlingAll(RequestMessage requestMessage) throws Exception {
        String destination = "/all";
        String sender = HtmlUtils.htmlEscape(requestMessage.getSender());  //htmlEscape  转换为HTML转义字符表示
        String type = HtmlUtils.htmlEscape(requestMessage.getType());
        String content = HtmlUtils.htmlEscape(requestMessage.getContent());
        ResponseMessage response = new ResponseMessage(sender, type, content);
 
        messagingTemplate.convertAndSend(destination, response);
    }
    
    
    
    
    @GetMapping("/test")
    public void test(String username){
        messagingTemplate.convertAndSendToUser(username, "/topic/greetings", "hello:"+username);
    }

 
 
}