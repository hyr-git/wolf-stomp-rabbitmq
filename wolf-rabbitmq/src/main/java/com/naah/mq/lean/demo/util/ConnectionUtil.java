package com.naah.mq.lean.demo.util;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 连接工具类
 */
public class ConnectionUtil {

	public String queueName;

	public Channel channel;
	
	Connection connection;
	
	
    /***
     * 通过指定的队列名称获取连接工具类
     * @param queueName
     * @throws IOException
     * @throws TimeoutException
     */
	public ConnectionUtil(String queueName) throws IOException, TimeoutException {
		this.queueName = queueName;
		// 创建连接工厂
		ConnectionFactory cf = new ConnectionFactory();
		// 设置rabbitmq服务器IP地址
		cf.setHost("127.0.0.1");
		// 设置rabbitmq服务器用户名
		cf.setUsername("admin");
		// 设置rabbitmq服务器密码
		cf.setPassword("admin");
		cf.setPort(AMQP.PROTOCOL.PORT);
		cf.setVirtualHost("push-server");
		
		// 获取一个新的连接
		connection = cf.newConnection();
		// 创建一个通道
		channel = connection.createChannel();
		/**
		 * 申明一个队列，如果这个队列不存在，将会被创建
		 * @param queue  队列名称
		 * @param durable  持久性：true队列会再重启过后存在，但是其中的消息不会存在。
		 * @param exclusive    是否只能由创建者使用
		 * @param autoDelete  是否自动删除（没有连接自动删除）
		 * @param arguments  队列的其他属性(构造参数)
		 * @return 宣告队列的声明确认方法已成功声明。
		 * @throws java.io.IOException    if an error is encountered
		 */
		channel.queueDeclare(queueName, true, false, false, null);
	}

	public void close() throws IOException, TimeoutException {
		channel.close();
		connection.close();
	}
}