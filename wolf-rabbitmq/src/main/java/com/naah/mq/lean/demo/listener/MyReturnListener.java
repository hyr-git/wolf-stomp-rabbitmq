package com.naah.mq.lean.demo.listener;

import java.io.IOException;

import org.springframework.amqp.utils.SerializationUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ReturnListener;

/**
 * 实现此接口以通知交付basicpublish失败时，“mandatory”或“immediate”的标志监听（源代码注释翻译）。
 * 在发布消息时设置mandatory等于true，监听消息是否有相匹配的队列，
 * 没有时ReturnListener将执行handleReturn方法，消息将返给发送者 。
 * 由于3.0版本过后取消了支持immediate，此处不做过多的解释。
 */
public class MyReturnListener implements ReturnListener {

	@Override
	public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
			BasicProperties properties, byte[] body) throws IOException {
		System.out.println("消息发送到队列失败：回复失败编码：" + replyCode + ";回复失败文本：" + replyText + ";失败消息对象："
				+ SerializationUtils.deserialize(body));
	}

}
