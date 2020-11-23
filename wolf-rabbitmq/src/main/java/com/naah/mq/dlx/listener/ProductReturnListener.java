package com.naah.mq.dlx.listener;

import java.io.IOException;

import org.springframework.amqp.utils.SerializationUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ReturnListener;

/** 实现此接口以通知交付basicpublish失败时，“mandatory”或“immediate”的标志监听（源代码注释翻译）。
 * 在发布消息时设置mandatory等于true，监听消息是否有相匹配的队列，
 * 没有时ReturnListener将执行handleReturn方法，消息将返给发送者
 */
public class ProductReturnListener implements ReturnListener{

	@Override
	public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, BasicProperties properties, byte[] body)
			throws IOException {
		System.out.println("消息发送到队列失败：回复失败编码："+replyCode+";回复失败文本："+replyText+";失败消息对象："+SerializationUtils.deserialize(body));
	}

}
