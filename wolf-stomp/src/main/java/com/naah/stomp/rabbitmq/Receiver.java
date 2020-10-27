package com.naah.stomp.rabbitmq;

import java.io.IOException;
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
       // amqpTemplate.convertAndSend( destination, context);
        System.out.println("processToWeb  : " + JSON.toJSONString(message));

        messagingTemplate.convertAndSend( "/"+RabbitConfig.TO_WEB_QUEUE_NAME, message);
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

       // amqpTemplate.convertAndSend( destination, context);
        messagingTemplate.convertAndSend( "/"+RabbitConfig.TO_APP_QUEUE_NAME, message);
/*        RequestMessage mqTask = new RequestMessage(  );
        BeanUtils.copyProperties( JsonUtils.jsonToObject( context,RequestMessage.class ),mqTask );

        if (Objects.equals( mqTask.getType(), "1" )) {
            Record record = new Record( RandomUtils.number( 9 ),
                                        mqTask.getUserId(),
                                        mqTask.getQuestionId(),
                                        mqTask.getContent(),
                                        null,null,
                                        TimeUtils.getNow());
            //recordService.saveOrUpdate( record );
        }

        if (Objects.equals( mqTask.getType(), "2" )) {
            String destination = "/topic/" +mqTask.getRoom();

            messagingTemplate.convertAndSend( destination, mqTask);
        }
*/
    }

}
