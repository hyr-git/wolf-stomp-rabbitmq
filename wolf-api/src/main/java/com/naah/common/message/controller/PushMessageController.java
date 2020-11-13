package com.naah.common.message.controller;

import java.util.Date;
import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naah.mq.component.DirectSender;
import com.naah.mq.config.DirectRabbitConfig;
import com.naah.mq.model.MQMessage;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class PushMessageController {

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Autowired
	private DirectSender directSender;

	/****
	 * 推送消息到MQ服务器
	 * @param msg
	 */
	@ApiOperation(value = "推送消息到MQ服务器", notes = "推送消息到MQ服务器")
	@ApiImplicitParam(name = "MQMessage", value = "发送的消息", required = true)
	@RequestMapping("/sendMsg")
	public void sendMsg(@RequestBody MQMessage message) {
		//TODO 消息处理
		//不同类型的业务消息不同的处理逻辑 work-order-queue
		//message.getType()  推送给具体的队列
		message.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		message.setCreatedDate(new Date());
		amqpTemplate.convertAndSend(DirectRabbitConfig.WORK_ORDER_QUEUE_NAME, message);
	}
	
	
	/****
	 * 推送消息到MQ服务器
	 * @param msg
	 */
	@ApiOperation(value = "推送消息到MQ服务器", notes = "推送消息到MQ服务器")
	@ApiImplicitParam(name = "MQMessage", value = "发送的消息", required = true)
	@RequestMapping("/sendSimpleMsg")
	public void sendMsg(String message) {
		//TODO 消息处理
		//不同类型的业务消息不同的处理逻辑 work-order-queue
		//message.getType()  推送给具体的队列
		amqpTemplate.convertAndSend("topic", message+"-----"+UUID.randomUUID());
	}
	
	/****
	 * 某人某时读取了某条消息
	 * @param msg
	@PostMapping("/readMsg")
	public void readMsg(@RequestBody ReadMessageDTO ReadMessageDTO) {
		//TODO 更新数据库中的消息状态变更为已读
		//amqpTemplate.convertAndSend(DirectRabbitConfig.WORK_ORDER_QUEUE_NAME, "a", msg);
	} */
	
	
	@RequestMapping("/push")
	public void pushMsg(String msg) {
		directSender.sendMsg(msg);
	}
}
