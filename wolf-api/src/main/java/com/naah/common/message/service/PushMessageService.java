package com.naah.common.message.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.carsir.common.message.component.JPushManage;
import com.carsir.common.message.config.RabbitConfig;
import com.carsir.common.model.MQMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PushMessageService extends JPushManage{

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	/****
	 * TODO 离线消息暂不考虑
	 * web推送消息到MQ服务器
	 * @param msg
	 */
	public void sendWebToAppMsg(MQMessage message) {
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
	public void sendAPPToWebMsg(MQMessage message) {
		//TODO 消息处理
		//不同类型的业务消息不同的处理逻辑 work-order-queue
		//message.getType()  推送给具体的队列
		message.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		message.setCreatedDate(new Date());
		amqpTemplate.convertAndSend(RabbitConfig.TO_WEB_QUEUE_NAME, message);
	}
	
  public void sendMessageByTopic(String topicName,MQMessage message){
	  log.info("current time is :"  + System.currentTimeMillis());
    amqpTemplate.convertAndSend(topicName, "current time is :" + System.currentTimeMillis());
  }
  
  public void sendStrMessageByTopic(String topicName,String message){
	    log.info("current time is :"  + System.currentTimeMillis());
	    amqpTemplate.convertAndSend(topicName, "current time is :" + System.currentTimeMillis());
	  }
}
