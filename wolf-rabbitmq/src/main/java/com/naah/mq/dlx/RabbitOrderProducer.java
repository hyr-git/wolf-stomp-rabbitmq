package com.naah.mq.dlx;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.AMQP.Exchange;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 生产者
 * <p>
 * 死信队列和延时消息的使用
 * 消息先到 order_queue 中，然后 10s 钟没有消费，消息流转到死信队列 dlx.queue 中
 *
 * @author huan.fu
 * @date 2018/10/15 - 14:10
 */
@Slf4j
public class RabbitOrderProducer {

	private static final String IP_ADDRESS = "127.0.0.1";
	private static final int PORT = 5672;
	
	private static String ORDER_EXCHANGE_NAME = "order_exchange";
	private static String ORDER_QUEUE_NAME = "order_queue";
	private static String ORDER_ROUTING_KEY = "order.#";
	
	private static String DLX_EXCHANGE_NAME = "dlx.exchange";
	private static String DLX_QUEUE_NAME = "dlx.queue";
	

	private static Connection getConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("admin");
		factory.setHost(IP_ADDRESS);
		factory.setPort(PORT);
		factory.setPassword("admin");
		factory.setVirtualHost("push-server");
		return factory.newConnection();
	}
	
	public static void main(String[] args) throws IOException, TimeoutException {
		// 创建一个连接
		Connection connection = getConnection();
		Channel channel = null;
		try {
			/*
			 * 1、创建信道
			 */
			 channel = connection.createChannel();
			
			/*
			 * 2、初始化topic类型的持久化非自动删除的交换器,指定交换器的名称、类型、是否持久化、是否自动删除
			 */
			Exchange.DeclareOk declareOk = channel.exchangeDeclare(ORDER_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);
		
			Map<String, Object> arguments = new HashMap<>(16);
			//为队列设置队列交换器
			arguments.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
			//设置队列中的消息 10s 钟后过期
			arguments.put("x-message-ttl", 10000);
			//将过期等删除的消息推送到指定交换机的指定路由键的队列中去
			//arguments.put("x-dead-letter-routing-key", "为 dlx exchange 指定路由键，如果没有特殊指定则使用原队列的路由键");
				
			/**
			 * 
			 * 3、初始化队列--持久化、非排他、非自动删除
			 *   指定队列名称
			 *   durable 是否持久化,队列的声明默认是放在内存中的,重启之后会丢失;若持久化会保存在erlang自带的mnesia数据库中
			 *   exclusive 是否排他,一般有2个作用,a当连接关闭的时候该队列是否自动删除;
			 *       b是该队列是否是私有的private,若不是排外，可以使用2个消费者都访问同一个队列,
			 *       否则排外会对当前队列加锁,其他通道channel是不能访问的，强制访问会报错。
			 *       一般等于true用于一个队列只能有一个消费者来消费的场景。 
			 *   autoDelete当最后一个消费者断开连接之后是否自动删除队列 
			 *   arguments 参数集合
			 *     x-expires：当队列在指定的时间没有被访问就会被删除；
			 *     x-max-length：用于限定队列消息的最大值长度,超过指定的长度会把最早的几条删除；
			 *     x-message-ttl: 设置队列中所有消息的生存周期,时间到了消息会被删除；
			 *     x-dead-letter-exchange：当消息长度大于最大长度、超时过期等,将从队列中删除的消息推送到指定的交换机中去；
			 *     x-dead-letter-routing-key：将删除的消息推送到指定交换机的指定路由键的队列中去；
			 *
			 *扩展知识：
			 *    queueDeclareNoWait表示不需要服务端的任何返回
			 *    queueDeclarePassive(queue)检测相应的队列是否存在,有就正常返回,不存在就抛出异常404,channel excepton 同时channel也会被关闭。
			 *    queueDelete(String queue, boolean ifUnused, boolean ifEmpty) ifUnused设置是否在队列没有使用情况下删除,true只有在此队列没有被使用的情况下才删除。
			 */
			channel.queueDeclare(ORDER_QUEUE_NAME, true, false, false, arguments);
			
			/*
			 * 4、队列绑定交换器
			 */
			channel.queueBind(ORDER_QUEUE_NAME, ORDER_EXCHANGE_NAME, ORDER_ROUTING_KEY);
			String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 创建订单.";

			// 创建死信交换器和队列
			channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);
			channel.queueDeclare(DLX_QUEUE_NAME, true, false, false, null);
			channel.queueBind(DLX_QUEUE_NAME, DLX_EXCHANGE_NAME, ORDER_ROUTING_KEY);
			
			//TODO 单独为某条消息设置过期时间
			//AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder().expiration("6000");
			//channel.basicPublish(orderExchangeName, "order.save", properties.build(), message.getBytes(StandardCharsets.UTF_8));
			
		    //TODO 为指定该队列设置所有的过期时间
			channel.basicPublish(ORDER_EXCHANGE_NAME, "order.save", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

			log.info("消息发送完成......");
			
		}finally {
			/**
	         * 关闭资源
	         */
			channel.close();
	        connection.close();
		}
	}
}
