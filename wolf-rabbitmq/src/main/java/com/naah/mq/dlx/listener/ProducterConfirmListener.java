package com.naah.mq.dlx.listener;

import java.io.IOException;

import com.rabbitmq.client.ConfirmListener;

/***
 * 自定义的消息确认监听器
 * @author mlj
 *
 */
public class ProducterConfirmListener implements ConfirmListener{

	/**
	 * 生产者发送消息到exchange成功的回调方法
	 * 消息被接搜狐了以后,如果没有匹配的Queue，则会被抛弃，但是可以设置ReturnListener监听来处理有没有匹配的队列。
	 * 因此handleAck执行了,并不能标识消息已经发送进入了对应的队列，只能标识对应的exchange已经接收了消息，
	 * 因为交换器接受了消息之后还要经过bingKey等一套匹配规则之后才会发到对应的队列
	 */
	@Override
	public void handleAck(long deliveryTag, boolean multiple) throws IOException {
		//注意：deliveryTag是broker给消息指定的唯一id（从1开始）
		System.out.println("Exchange接收消息："+deliveryTag+"（deliveryTag）成功！multiple="+multiple);
	}

	/**
	* 生产者发送消息到服务器broker失败的回调方法，服务器丢失了此消息。
	* 注意，丢失的消息仍然可以传递给消费者，但broker不能保证这一点。（不明白，既然丢失了，为啥还能发送）
	*/
	public void handleNack(long deliveryTag, boolean multiple) throws IOException {
		System.out.println("Exchange接收消息："+deliveryTag+"（deliveryTag）失败！服务器broker丢失了消息");
	}
	
}
