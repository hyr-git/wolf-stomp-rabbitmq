package com.naah.mq.lean.demo.consumer;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.utils.SerializationUtils;

import com.naah.mq.lean.demo.util.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

/**
 * 专门处理奇数消息的消费者
 */
public class OddConsumer extends MessageConsumer {

	public OddConsumer(ConnectionUtil connectionUtil) {
		super(connectionUtil);
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
		this.consumerTag = consumerTag;
		System.out.println("OddConsumer消费者：" + consumerTag + ",注册成功！");
	}

	@Override
	public void handleDelivery(String arg0, Envelope envelope, BasicProperties arg2, byte[] body) throws IOException {
		if (body == null)
			return;
		Map<String, Object> map = (Map<String, Object>) SerializationUtils.deserialize(body);
		int tagId = (Integer) map.get("tagId");
		if (tagId % 2 != 0) {
			// 处理消息
			System.out.println("OddConsumer接收并处理消息：" + tagId);
			// 通知服务器此消息已经被处理了
			connectionUtil.channel.basicAck(envelope.getDeliveryTag(), false);
		} else {
			// 通知服务器消息处理失败，重新放回队列。false表示处理失败消息不放会队列，直接删除
			connectionUtil.channel.basicReject(envelope.getDeliveryTag(), true);
		}
	}
}