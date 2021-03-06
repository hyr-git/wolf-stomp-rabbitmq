package com.naah.common.mq.component;

import java.util.Date;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/***
 * 生产者
 * @author m1832
 *
 */
@Component
public class DirectSender {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void sendMsg(String msg) {
		String sengMsg = "hello, " + new Date() +"------"+msg;
		System.out.println("DirectSender: "+sengMsg);
		rabbitTemplate.convertAndSend("WORK_ORDER_QUEUE_NAME",sengMsg);
	}
}

