package com.naah.mq.lean.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.naah.mq.lean.demo.consumer.EvenConsumer;
import com.naah.mq.lean.demo.consumer.OddConsumer;
import com.naah.mq.lean.demo.listener.MyConfirmListener;
import com.naah.mq.lean.demo.listener.MyReturnListener;
import com.naah.mq.lean.demo.producer.MessageProducer;
import com.naah.mq.lean.demo.util.ConnectionUtil;

public class Client {

	public static void main(String[] args) throws TimeoutException {
		new Client();
	}

	public Client() throws TimeoutException {
		try {
			// 发消息
			publishMessage();
			// 注册消费者
			addConsumer();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void publishMessage() throws IOException, InterruptedException, TimeoutException {
		ConnectionUtil connectionUtil = new ConnectionUtil("test-queue");
		MessageProducer producer = new MessageProducer(connectionUtil);
		connectionUtil.channel.confirmSelect();
		// 注意：返回的时候Return在前，Confirm在后
		connectionUtil.channel.addConfirmListener(new MyConfirmListener());
		connectionUtil.channel.addReturnListener(new MyReturnListener());
		int i = 1;
		while (i <= 10) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("tagId", i);
			producer.sendMessage(map);
			i++;
		}
	}

	public void addConsumer() throws IOException, TimeoutException {
		ConnectionUtil connectionUtil = new ConnectionUtil("testqueue");
		OddConsumer odd = new OddConsumer(connectionUtil);
		odd.basicConsume();
		EvenConsumer even = new EvenConsumer(connectionUtil);
		even.basicConsume();
	}

}