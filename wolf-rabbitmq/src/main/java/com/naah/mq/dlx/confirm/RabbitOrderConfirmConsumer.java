package com.naah.mq.dlx.confirm;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 消息消费者
 * @date 2018/8/13 - 15:39
 * 为了保证消息从队列中可靠的到达消费者,rabbitMQ提供消息确认机制,消费者在注册时，可以指定noAck参数,等待消费者显示发出ack信号后才从内存中种移除消息。
 * 否则rabbitMQ会在队列中消息被消费后立即删除它。
 * 
 * 当noAck=false,对于rabbitMQ服务器而言,队里中的消息分成2部分：
 *   一部分是在等待投递给消费者的消息(web界面上的Ready状态)；
 *   一部分是已经投递给消费者，还没有收到消费者ack信号的消息(web管理界面上的unacked状态)。
 *   
 *   若服务器一直没有收到消费者的ack信号，并且消费次消息的消费者已经断开连接,则服务器断案会安排该消息重新进入到队列,等待投递给下一个消费者(也有可能是原来那个投递者)
 *   
 *   
 **/
@Slf4j
public class RabbitOrderConfirmConsumer {

	private static final String IP_ADDRESS = "127.0.0.1";
	private static final String USER = "admin";
	private static final String PASSWD = "admin";

	private static String ORDER_EXCHANGE_NAME = "order_exchange";
	
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
			//整个管道最多64条
			channel.basicQos(64,true);
			
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
					log.info(">>>>>>>>接收到消息：" + new String(body, StandardCharsets.UTF_8));
					log.info(">>>>>>>>deliveryTag:" + envelope.getDeliveryTag());
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
			channel.basicConsume(ORDER_EXCHANGE_NAME,false, consumer);
			TimeUnit.SECONDS.sleep(10000000L);
			
			channel.close();
		    connection.close();
		}
	}
}