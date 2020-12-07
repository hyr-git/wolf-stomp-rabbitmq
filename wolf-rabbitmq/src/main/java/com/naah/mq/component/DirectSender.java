package com.naah.mq.component;

import java.util.Date;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/***
 * 生产者
 * @author m1832
 * direct 交换器是默认交换器。声明一个队列时，会自动绑定到默认交换器，并且以队列名称作为路由 键
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

