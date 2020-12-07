package com.naah.mq.dlx.confirm;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.naah.mq.dlx.listener.ProductReturnListener;
import com.naah.mq.dlx.listener.ProducterConfirmListener;
import com.rabbitmq.client.AMQP.Exchange;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 生产者
 * <p>
 * 死信队列和延时消息的使用
 * 消息先到 order_queue 中，然后 10s 钟没有消费，消息流转到死信队列 dlx.queue 中
 * @date 2018/10/15 - 14:10
 * https://www.cnblogs.com/yibutian/p/9469057.html
 * 
 * 
 * @备注
 *    消息确认机制
 *    消息生成这通过调用channel.confirmSelect()方法将Channel信道设置陈恒confirm模式。
 *    一旦设置成confirm模式，该信道上的所有消息都会被指派一个唯一的ID(从1开始)，一旦消息被对应的exchange接受，
 */
@Slf4j
public class RabbitOrderConfirmProducer {

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
			
			 //设置消息确认机制
			 /*
			  * 消息生产者通过调用channel.confirmSelect()方法或者将channel信道设置成confirm模式,一旦信道被设置成confirm模式,
			  * 该信道上的所有消息都会被指定一个唯一的消息id(从1开始),一旦交换器接收到对应的交换器,broker会发送一个确认之给对应的生产者(其中deliveryTag就是唯一的消息id)，
			  * 这样消息生成这就知道该消息已经被推送给对应的broker
			  * 
			  * confirm最大的优点就是在于他的异步(rabbitmq事务是同步性能太低),生产者在发送一条消息之后,会在等待确认的同时发送另外的消息；当消息得到确认，生产者会通过消息确认接口进行处理该回调信息，
			  * 如果因为rabbitmq内部的问题broker导致消息丢失,会发生一条nack消息,生产者同样可以处理回方法中处理该nack的消息。
			  * 
			  * 在channel设置为confirm之后,所有被publish的后续消息都被confirm(ack)或者nack一次，但是没有对消息被confirm的快慢做任何保证，并且同一条消息不会既被confirm又被nack.
			  *
			  * ####已经被设置里transaction的channel不能再被设置为confirm,即transaction与confirm不能并存#####
			  */
			 channel.confirmSelect();
			 
			 //注册消息确认监听器
			 channel.addConfirmListener(new ProducterConfirmListener());
			 
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
			Exchange.DeclareOk declareOk = channel.exchangeDeclare(ORDER_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);
		
			Map<String, Object> arguments = new HashMap<>(16);
			//为队列设置队列交换器
			arguments.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
			//设置队列中的消息 10s 钟后过期
			arguments.put("x-message-ttl", 10000);
			//将过期等删除的消息推送到指定交换机的指定路由键的队列中去
			//arguments.put("x-dead-letter-routing-key", "为 dlx exchange 指定路由键，如果没有特殊指定则使用原队列的路由键");
				
			/**
			 * 3、初始化队列--持久化、非排他、非自动删除
			 *   指定队列名称
			 * @param queueName 队列名称 
			 * @param durable 是否持久化,队列的声明默认是放在内存中的,重启之后会丢失;若持久化会保存在erlang自带的mnesia数据库中
			 * @param exclusive 是否排他,一般有2个作用,a当连接关闭的时候该队列是否自动删除;
			 *       b是该队列是否是私有的private,若不是排外，可以使用2个消费者都访问同一个队列,
			 *       否则排外会对当前队列加锁,其他通道channel是不能访问的，强制访问会报错。
			 *       一般等于true用于一个队列只能有一个消费者来消费的场景。 
			 * @param  autoDelete当最后一个消费者断开连接之后是否自动删除队列 
			 * @param  arguments 参数集合
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
			 * 将队列绑定到Exchange，不需要额外的参数。
			 * @param queue 队列名称
			 * @param exchange 交换机名称
			 * @param routingKey 路由关键字
			 * @return Queue.BindOk：如果成功创建绑定，则返回绑定确认方法。
			 * @throws java.io.IOException if an error is encountered
			 */
			channel.queueBind(ORDER_QUEUE_NAME, ORDER_EXCHANGE_NAME, ORDER_ROUTING_KEY);
			
			String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 创建订单.";

			//创建死信交换器和队列
			channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);
			channel.queueDeclare(DLX_QUEUE_NAME, true, false, false, null);
			channel.queueBind(DLX_QUEUE_NAME, DLX_EXCHANGE_NAME, ORDER_ROUTING_KEY);
			
			//TODO 单独为某条消息设置过期时间
			//AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder().expiration("6000");
			//channel.basicPublish(orderExchangeName, "order.save", properties.build(), message.getBytes(StandardCharsets.UTF_8));
			
		    /*
		     * TODO 
		     * 发布一条不用持久化的消息，且设置两个监听。
		     * @param exchange 消息交换机名称,空字符串将使用直接交换器模式，发送到默认的Exchange=amq.direct。此状态下，RoutingKey默认和Queue名称相同
		     * @param routingKey 路由关键字
		     * @param mandatory 监听是否有符合的队列,  失败确认
		     *       当mandatory为true时，exchange根据自身类型和消息routeKey无法找到符合条件的queue，那么会调用basic.return方法将消息返回给生产者（Basic.Return + Content-Header + Content-Body）；
		     *       当mandatory为false时，broker会直接将消息扔掉。 
		     * @param immediate 监听符合的队列上是有至少一个Consumer,
		     *       当immediate为true时，如果exchange在将消息路由到queue(s)时对应的queue上没有消费者，那么这条消息不会放入队列中。当与消息routeKey关联的所有queue（一个或者多个）都没有消费者时，该消息会通过basic.return方法返还给生产者
		     * @param BasicProperties 设置消息持久化：MessageProperties.PERSISTENT_TEXT_PLAIN是持久化；MessageProperties.TEXT_PLAIN是非持久化。
		     * @param body 消息对象转换的byte[]
		     * 
		     * @备注说明
		     *  1、当mandatory为true时,如果exchange根据自身类型和消息routeKey无法找到符合条件的queue，
		     *     那么会调用basic.return方法将消息返回给生产者（Basic.Return + Content-Header + Content-Body）；
		     *     当mandatory设置为false时，出现上述情形broker会直接将消息扔掉。
		     *     
		     *  2、当immediate为true时,若exchange在将消息路由到queue(s)时对应的queue上没有消费者，
		     *  那么这条消息不会放入队列中。当与消息routingKey关联的所有queue（一个或者多个）都没有消费者时，该消息会通过basic.return方法返还给生产者。
             * 
             * @概括来说，
             *     mandatory标志告诉服务器至少将该消息route到一个队列中，否则将消息返还给生产者；
             *     immediate标志告诉服务器如果该消息关联的queue上有消费者，则马上将消息投递给它，
             *     如果所有queue都没有消费者，直接把消息返还给生产者，不用将消息入队列等待消费者了。
             * 
             * 注意：在RabbitMQ3.0以后的版本里，去掉了immediate参数的支持，发送带immediate=true标记的publish会返回如下错误：
             *       com.rabbitmq.client.AlreadyClosedException: connection is already closed due to connection error;protocol method: #method<connection.close>(reply-code=540, reply-text=NOT_IMPLEMENTED - immediate=true, class-id=60, method-id=40)。
             * 为什么取消支持：immediate标记会影响镜像队列性能，增加代码复杂性，并建议采用“TTL”和“DLX”等方式替代。
		     */
			channel.basicPublish(ORDER_EXCHANGE_NAME, "order.save",true,false, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

			//注册消息结果返回监听器
			channel.addReturnListener(new ProductReturnListener());
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
