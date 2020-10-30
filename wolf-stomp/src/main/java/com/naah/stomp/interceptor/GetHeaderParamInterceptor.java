package com.naah.stomp.interceptor;

import java.util.LinkedList;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.naah.stomp.model.UserStomp;

/**
 * @author : hao
 * @description : websocket建立链接的时候获取headeri里认证的参数拦截器。
 * @time : 2019/7/3 20:42
 */
@Component
public class GetHeaderParamInterceptor extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                Object Authorization = ((Map) raw).get("Authorization");
                Object grp = ((Map) raw).get("grp");
                // 设置当前访问器的认证用户
                //accessor.setUser(new UserStomp(Authorization)));
            }
        }
        return message;
    }
}