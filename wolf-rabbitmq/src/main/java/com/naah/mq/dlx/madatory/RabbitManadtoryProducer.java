package com.naah.mq.dlx.madatory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Exchange;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Return;
import com.rabbitmq.client.ReturnCallback;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 生产者
 * <p>
 * 死信队列和延时消息的使用
 * 消息先到 order_queue 中，然后 10s 钟没有消费，消息流转到死信队列 dlx.queue 中
 * @date 2018/10/15 - 14:10
 * https://www.cnblogs.com/yibutian/p/9469057.html
 */
@Slf4j
public class RabbitManadtoryProducer {

	private static final String IP_ADDRESS = "127.0.0.1";
	private static final int PORT = 5672;
	
	static String ORDER_EXCHANGE_NAME = "order_manadtory_exchange";
	
	 //失败通知--当mandatory设置为true时,rabbitMQ接收到消息无法进行路由时,会将消息退回给生产者;否则设置为false会将消息直接丢弃。
    private final static boolean MANDATORY = true; 
	

	private static Connection getConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("admin");
		factory.setHost(IP_ADDRESS);
		factory.setPort(PORT);
		factory.setPassword("admin");
		factory.setVirtualHost("push-server");
		return factory.newConnection();
	}
	
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
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
			 * 声明一个队列不存在,则会被创建
			 * @param exchange  队列名称 
			 * @param type  exchange type：direct、fanout、topic、headers 
			 * @param durable 持久化：true队列会在重启过后存在(队列的消息需要单独设置持久化)
			 * @param autoDelete 是否自动删除(没有连接自动删除)
			 * @param arguments 队列的其他属性(构造参数)
			 * @return Queue.Declare：宣告队列的声明确认方法已成功声明
			 */
			Exchange.DeclareOk declareOk = channel.exchangeDeclare(ORDER_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		
			
			/*
			 * 4、队列绑定交换器--建议在消费端进行绑定
			 * 将队列绑定到Exchange，不需要额外的参数。
			 * @param queue 队列名称
			 * @param exchange 交换机名称
			 * @param routingKey 路由关键字
			 * @return Queue.BindOk：如果成功创建绑定，则返回绑定确认方法。
			 * @throws java.io.IOException if an error is encountered
			 */
			//channel.queueBind(ORDER_QUEUE_NAME, ORDER_EXCHANGE_NAME, ORDER_ROUTING_KEY);
			
			String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 创建订单.";

			
			//TODO 单独为某条消息设置过期时间
			//AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder().expiration("6000");
			//channel.basicPublish(orderExchangeName, "order.save", properties.build(), message.getBytes(StandardCharsets.UTF_8));
			
			
			//监听通道关闭事件
			channel.addShutdownListener(new ShutdownListener() {
				@Override
				public void shutdownCompleted(ShutdownSignalException cause) {
                    log.error("channel通道关闭---"+cause.getMessage());					
				}
			});
			
			//连接通道关闭事件
			connection.addShutdownListener(new ShutdownListener() {
				@Override
				public void shutdownCompleted(ShutdownSignalException cause) {
                    log.error("connection连接关闭---"+cause.getMessage());					
				}
			});
			
			
		    //TODO 失败通知回调
			channel.addReturnListener(new ReturnListener() {
				@Override
				public void handleReturn(int replycode, String replyText, String exchange, String routeKey, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
	                String message = new String(bytes);
	                log.error("返回的replycode:"+replycode);
	                 log.error("返回的replyText:"+replyText);
	                 log.error("返回的exchange:"+exchange);
	                 log.error("返回的routeKey:"+routeKey);
	                 log.error("返回的消息message:"+message);
	            }
			});
			
			 //TODO 失败通知回调
			channel.addReturnListener(new ReturnCallback() {
				@Override
				public void handle(Return returnMessage) {
					 log.error("ReturnCallback---->"+JSONObject.toJSONString(returnMessage));
					
				}
			});
			
		    /*
		     * TODO 
		     * 发布一条不用持久化的消息，且设置两个监听。
		     * @param exchange 消息交换机名称,空字符串将使用直接交换器模式，发送到默认的Exchange=amq.direct。此状态下，RoutingKey默认和Queue名称相同
		     * @param routingKey 路由关键字
		     * @param mandatory 监听是否有符合的队列,
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
			//channel.basicPublish(ORDER_EXCHANGE_NAME, "order.save1",MANDATORY,false, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
			  String[] routekeys={"king","mark","james"};
		        for(int i=0;i<3;i++){
		            String routekey = routekeys[i%3];
		            // 发送的消息
		             message = "Hello World_"+(i+1)
		                    +("_"+System.currentTimeMillis());
		            //TODO
		            channel.basicPublish(ORDER_EXCHANGE_NAME,routekey,MANDATORY,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes(StandardCharsets.UTF_8));
		            System.out.println("----------------------------------");
		            System.out.println(" Sent Message: [" + routekey +"]:'"
		                    + message + "'");
		            Thread.sleep(200);
		        }
			
			
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
