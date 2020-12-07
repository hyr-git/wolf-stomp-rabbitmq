package com.naah.mq.dlx;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

public class RabbitWorkOrderSender {

	private static String WORK_ORDER_EXCHANGE_NAME = "work_order_exchange";
	private static String WORK_ORDER_QUEUE_NAME = "work_order_queue";
	private static String WORK_ORDER_ROUTING_KEY = "work.order.#";
	
	private static String DLX_EXCHANGE_NAME = "dlx.exchange";
	private static String DLX_QUEUE_NAME = "dlx.queue";
	
	private static Connection getConnection() throws IOException, TimeoutException {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setPort(5672);
		factory.setUsername("admin");
		factory.setPassword("admin");
		factory.setVirtualHost("push-server");
		
		factory.setExceptionHandler(new DefaultExceptionHandler(){
	            @Override
	            public void handleConfirmListenerException(Channel channel, Throwable exception) {
	                System.out.println("=====消息确认发生异常=======");
	                exception.printStackTrace();
	            }
	        });
		
		return factory.newConnection();
	}
	
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		Connection connection = getConnection();
		Channel channel = null;
		
		String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 创建工单work_order."+UUID.randomUUID();

		
		try {
			//1、创建通道
			channel = connection.createChannel();
			
			//2、创建持久化的非自动删除的topic类型交换器
			channel.exchangeDeclare(WORK_ORDER_EXCHANGE_NAME, BuiltinExchangeType.TOPIC,true,false,null);
			
			Map<String, Object> arguments = new HashMap<>();
			arguments.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
			//设置队列中的消息 10s 钟后过期
			arguments.put("x-message-ttl", 10000);
			//3、声明队列
			channel.queueDeclare(WORK_ORDER_QUEUE_NAME, true, false, false, arguments);
			
			//4、绑定队列
			channel.queueBind(WORK_ORDER_QUEUE_NAME, WORK_ORDER_EXCHANGE_NAME, WORK_ORDER_ROUTING_KEY);
           
			/// 创建死信交换器和队列
			channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);
			channel.queueDeclare(DLX_QUEUE_NAME, true, false, false, null);
			channel.queueBind(DLX_QUEUE_NAME, DLX_EXCHANGE_NAME, WORK_ORDER_ROUTING_KEY);
			
			channel.addConfirmListener(new ConfirmListener() {
	            @Override
	            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
	                System.out.println("收到消息确认，："+deliveryTag);
	            }

	            @Override
	            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
	            	  throw new IOException("数据库异常，确认失败");
	            }
	        });			
			
			//5、发送消息
			channel.basicPublish(WORK_ORDER_EXCHANGE_NAME, "work.order.save", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
			
			System.out.println("消息发送出去----->");
			//TimeUnit.SECONDS.sleep(20);
			channel.close();
			connection.close();
		} finally {
			// TODO: handle finally clause
			
		}
	}
}
