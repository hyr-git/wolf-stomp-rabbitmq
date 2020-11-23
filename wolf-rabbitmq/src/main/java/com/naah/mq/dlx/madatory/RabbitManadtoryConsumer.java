package com.naah.mq.dlx.madatory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.Exchange;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 消息消费者—失败确认模式(消费者只绑定了king)
 * @date 2018/8/13 - 15:39
 */
@Slf4j
public class RabbitManadtoryConsumer {

	private static final String IP_ADDRESS = "127.0.0.1";
	private static final String USER = "admin";
	private static final String PASSWD = "admin";

	
	private static final int PORT = 5672;

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		Address[] addresses = new Address[]{new Address(IP_ADDRESS, PORT)};
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(USER);
		connectionFactory.setPassword(PASSWD);
		connectionFactory.setVirtualHost("push-server");

		try (
				// 注意此时获取连接的方式和生产者略有不同
				Connection connection = connectionFactory.newConnection(addresses)
		) {
			// 创建信道
			Channel channel = connection.createChannel();
			
			// 消费端消息限流。
			/*
			 * 设置客户端最多接收未被ack的消息个数, 只有消息 手动签收  此参数才会生效。
			 * 设置消费批量投递数目,一次性投递64条消息,当消费者未确认消息累计达到64条,rabbitMQ将不会向此channel上的消费者投递消息,直到为确认数小于64条再投递
			 * @param prefetchCount 投递数目
             * @param global 是否针对整个Channel。true表示此投递数是给Channel设置的，false是给Channel上的Consumer设置的。
             * @throws java.io.IOException if an error is encountered
			 */
		
			channel.basicQos(16,false);//单个管道最多16条
			channel.basicQos(64,true);	//整个管道最多64条
			
			//消费者直接生成一个随机的队列
	        String queueName = channel.queueDeclare().getQueue();
			
	        
			/*
			 * 2、初始化topic类型的持久化非自动删除的交换器,指定交换器的名称、类型、是否持久化、是否自动删除
			 * 声明一个队列不存在,则会被创建
			 * @param exchange  队列名称 
			 * @param type  exchange type：direct、fanout、topic、headers 
			 * @param durable 持久化：true队列会在重启过后存在(队列的消息需要单独设置持久化)
			 * @param autoDelete 是否自动删除(没有连接自动删除)
			 * @param arguments 队列的其他属性(构造参数)
			 * @return Queue.Declare：宣告队列的声明确认方法已成功声明
			 */
			//Exchange.DeclareOk declareOk = channel.exchangeDeclare(RabbitManadtoryProducer.ORDER_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		      
	        //绑定队列与交换器---只关注king的
	        String routekey="king";
	        channel.queueBind(queueName, RabbitManadtoryProducer.ORDER_EXCHANGE_NAME,routekey);
			
			Consumer consumer = new DefaultConsumer(channel) {
				/**consumer处理消息
				 * @param consumerTag 消息标签
				 * @param enevlope 消息的包装数据
				 * @param properties 消息的内容参数
				 * @param body 消息对象的body 
				 * @throws IOException
				 */
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, StandardCharsets.UTF_8);
					//记录日志到文件：
	                System.out.println( "Received ["+ envelope.getRoutingKey()
	                        + "] "+message);
					log.info(">>>>>>>>接收到消息：" + message);
					log.info(">>>>>>>>deliveryTag:" + envelope.getDeliveryTag());
	                System.out.println("envelop----->"+envelope.toString());
					//确认消息
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			};

			/**
			* 开始一个非局部、非排他性消费， with a server-generated consumerTag.
			* 执行这个方法会回调handleConsumeOk方法
			* @param queue 队列名称
			* @param autoAck 是否自动应答。
			*        false表示consumer在成功消费过后必须要手动回复一下服务器，
			*        如果不回复，服务器就将认为此条消息消费失败，继续分发给其他consumer。
			* @param callback 回调方法类，一般为自己的Consumer类
			* @return 由服务器生成的consumertag
			* @throws java.io.IOException if an error is encountered
			*/
			channel.basicConsume(queueName,false, consumer);
			
			
			/* // 创建队列消费者
	        final Consumer consumerB = new DefaultConsumer(channel) {
	            @Override
	            public void handleDelivery(String consumerTag,
	                                       Envelope envelope,
	                                       AMQP.BasicProperties properties,
	                                       byte[] body) throws IOException {
	                String message = new String(body, "UTF-8");
	                
	                System.out.println("envelop----->"+envelope.toString());
	                //记录日志到文件：
	                System.out.println( "Received ["+ envelope.getRoutingKey()
	                        + "] "+message);
	            }
	        };*/
	        //channel.basicConsume(queueName, true, consumerB);
			
			TimeUnit.SECONDS.sleep(10000000L);
			
			channel.close();
		    connection.close();
		}
	}
}