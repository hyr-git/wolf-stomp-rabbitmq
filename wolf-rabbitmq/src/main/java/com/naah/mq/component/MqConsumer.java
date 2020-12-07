package com.naah.mq.component;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import io.reactivex.netty.protocol.http.websocket.WebSocketServer;

import java.io.IOException;

/**
 * 消息监听器
 * @description
 */

@Component
public class MqConsumer {
    private static final Logger log = LoggerFactory.getLogger(MqConsumer.class);

    /**
     * FANOUT广播队列监听一.
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception  这里异常需要处理
     */
    @RabbitListener(queues = {"FANOUT_QUEUE_A"})
    public void on(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        String responsJson = new String(message.getBody());
        log.debug("consumer FANOUT_QUEUE_A : " + responsJson);

        /*//发送给浏览器
        if (StringUtils.isNotBlank(responsJson)) {
            //SocketMessageDTO dto = JSONUtil.toBean(responsJson, SocketMessageDTO.class);
            String userId = dto.getUserId();
            String msg = dto.getMessage();

            if (WebSocketServer.getSessionmap().get(userId) != null) {
                try {
                	//推送消息到页面
                    WebSocketServer.sendInfo(msg, userId);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("socket send error,userId=" + userId + "#" + e.getMessage());
                }
            }
        }*/
    }

    /**
     * FANOUT广播队列监听二.
     *
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception   这里异常需要处理
     */
    @RabbitListener(queues = {"FANOUT_QUEUE_B"})
    public void t(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.debug("FANOUT_QUEUE_B " + new String(message.getBody()));
    }

    /**
     * DIRECT模式.
     *
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception  这里异常需要处理
     */
    @RabbitListener(queues = {"DIRECT_QUEUE"})
    public void message(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.debug("DIRECT " + new String(message.getBody()));
    }
}