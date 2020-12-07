package com.naah.mq.lean.demo.consumer;

import java.io.IOException;

import com.naah.mq.lean.demo.util.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 消息消费者基础类
 */
public class MessageConsumer implements Consumer {
	
	// 消费者标签，注册成功时由rabbitmq服务器自动生成
	protected String consumerTag;

	protected ConnectionUtil connectionUtil;

	public MessageConsumer(ConnectionUtil connectionUtil) {
		this.connectionUtil = connectionUtil;
	}

	public void basicConsume() {
		try {
			/**
			 * 设置消费投递数目，一次性投递10条消息。当消费者未确认消息达到10条时，rabbitMQ将不会向此消费者投递消息，直到未确认数小于10条再投递
			 * @param prefetchCount   投递数目
			 * @param global  是否针对整个Channel。true表示此投递数是给Channel设置的，false是给Channel上的Consumer设置的。
			 * @throws java.io.IOException   if an error is encountered
			 */
			connectionUtil.channel.basicQos(10, false);// 表示每个消费者最多10条
			connectionUtil.channel.basicQos(15, true);// 整个传输管道最多15条，具体分到每个消费者身上又不能大于10条
			
			/**
			 * 开始一个非局部、非排他性消费， with a server-generated consumerTag.
			 * 执行这个方法会回调handleConsumeOk方法
			 * @param queue  队列名称
			 * @param autoAck  是否自动应答。
			 *        false表示consumer在成功消费过后必须要手动回复一下服务器，如果不回复，服务器就将认为此条消息消费失败，继续分发给其他consumer。
			 * @param callback 回调方法类
			 * @return 由服务器生成的consumertag
			 * @throws java.io.IOException
			 *             if an error is encountered
			 */
			connectionUtil.channel.basicConsume(connectionUtil.queueName, false, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 收到消息时的回调函数
	 */
	public void handleDelivery(String arg0, Envelope arg1, BasicProperties arg2, byte[] arg3) throws IOException {
		// 子类重写覆盖具体操作
	}

	/**
	 * 消费者注册成功回调函数
	 */
	public void handleConsumeOk(String consumerTag) {
		this.consumerTag = consumerTag;
		System.out.println("消费者：" + consumerTag + ",注册成功！");
	}

	/**
	 * 手动取消消费者注册成功回调函数 当调用Channel类的void basicCancel(String consumerTag) throws
	 * IOException;方法触发此回调函数
	 */
	public void handleCancelOk(String consumerTag) {
		System.out.println(consumerTag + " 手动取消消费者注册成功!");
	}

	/**
	 * 当消费者因为其他原因被动取消注册时调用，比如queue被删除了。
	 */
	public void handleCancel(String consumerTag) throws IOException {
		System.out.println("因为外部原因消费者：" + consumerTag + " 取消注册！");
	}

	/**
	 * 当通道或基础连接被关闭时调用
	 */
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
		System.out.println("通道或基础连接被关闭");
	}

	/**
	 * Called when a <code><b>basic.recover-ok</b></code> is received in reply to a
	 * <code><b>basic.recover</b></code>. All messages received before this is
	 * invoked that haven't been <i>ack</i>'ed will be re-delivered. All messages
	 * received afterwards won't be.
	 * 
	 * @param consumerTag
	 *            the <i>consumer tag</i> associated with the consumer
	 */
	public void handleRecoverOk(String consumerTag) {

	}
}
