package com.naah.stomp.rabbitmq;

import java.util.Date;
import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naah.common.message.config.RabbitConfig;
import com.naah.common.message.model.MQMessage;

@RestController
public class PushMessageController {

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	
	/****
	 * 推送消息到MQ服务器
	 * @param msg
	 */
	@RequestMapping("/sendSimpleMsg")
	public void sendMsg(String message) {
		//TODO 消息处理
		//不同类型的业务消息不同的处理逻辑 work-order-queue
		//message.getType()  推送给具体的队列
		amqpTemplate.convertAndSend("topic", message+"-----"+UUID.randomUUID());
	}
	
	
	/****
	 * TODO 离线消息暂不考虑
	 * web推送消息到MQ服务器
	 * @param msg
	 */
	@RequestMapping("/webToApp")
	public void sendWebToAppMsg(@RequestBody MQMessage message) {
		//TODO 消息处理
		//不同类型的业务消息不同的处理逻辑 work-order-queue
		//message.getType()  推送给具体的队列
		message.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		message.setCreatedDate(new Date());
		amqpTemplate.convertAndSend(RabbitConfig.TO_APP_QUEUE_NAME, message);
	}
	
	/****
	 * TODO 离线消息暂不考虑
	 * web推送消息到MQ服务器
	 * @param msg
	 */
	@RequestMapping("/appToWeb")
	public void sendAPPToWebMsg(@RequestBody MQMessage message) {
		//TODO 消息处理
		//不同类型的业务消息不同的处理逻辑 work-order-queue
		//message.getType()  推送给具体的队列
		message.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		message.setCreatedDate(new Date());
		amqpTemplate.convertAndSend(RabbitConfig.TO_WEB_QUEUE_NAME, message);
	}
	
	
	
	/****
	 * 某人某时读取了某条消息
	 * @param msg
	@PostMapping("/readMsg")
	public void readMsg(@RequestBody ReadMessageDTO ReadMessageDTO) {
		//TODO 更新数据库中的消息状态变更为已读
		//amqpTemplate.convertAndSend(DirectRabbitConfig.WORK_ORDER_QUEUE_NAME, "a", msg);
	} */

    // 用于定时向topic发送消息
  @Scheduled(cron = "*/5 * * * * *")
  public void sendMessage(){
    System.out.println("current time is :"  + System.currentTimeMillis());
    amqpTemplate.convertAndSend("/topic/hello", "current time is :" + System.currentTimeMillis());
  }
	
}
