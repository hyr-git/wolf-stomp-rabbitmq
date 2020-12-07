package com.naah.mq.config;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RabbitConfig {
	
	@Resource
	private RabbitTemplate  rabbitTemplate;
	
	@Bean
	public AmqpTemplate aqmpTemplate() {
		rabbitTemplate.setEncoding("UTF-8");
		
		//消息发送失败需要返回到队列中,或者在配置文件中需要publisher-returns: true
		rabbitTemplate.setUsePublisherConnection(true);
	
		//mandatory为true,若exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息,
		//那么broker会调用basic.return方法将消息返回给生产者
		//当mandatory设置为false时,出现上述问题broker会直接将消息丢弃。
		//通俗的讲,mandatory标志告诉broker代理服务器至少将消息route到一个队列中,否则将消息return给发送者
		rabbitTemplate.setMandatory(true);
		
		rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String correlationId = message.getMessageProperties().getCorrelationId();
            log.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
        });
        // 消息确认，yml需要配置 publisher-confirms: true
        // 1.消费者确认 2.exchange没有路由到queue
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送到exchange成功,id: {}", correlationData.getId());
            } else {
                log.debug("消息发送到exchange失败,原因: {}", cause);
            }
        });
        return rabbitTemplate;
	}
	
	
	/**
     * 声明 topic 交换机.
     * @return the exchange
     */
    @Bean("topicExchange")
    public TopicExchange topicExchange() {
        return (TopicExchange) ExchangeBuilder.topicExchange("TOPIC_EXCHANGE").durable(true).build();
    }

    /**
     * Fanout queue A.
     *
     * @return the queue
     */
    @Bean("topicQueueA")
    public Queue topicQueueA() {
        return QueueBuilder.durable("TOPIC_QUEUE_A").build();
    }


    /**
     * 绑定队列A 到Topic 交换机.
     *
     * @param queue          the queue
     * @param topicExchange the topic exchange
     * @return the binding
     */
    @Bean
    public Binding topicBinding(@Qualifier("topicQueueA") Queue queue,
                            @Qualifier("topicExchange") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with("TOPIC.ROUTE.KEY.*");
    }

    /* ----------------------------------------------------------------------------Fanout exchange test--------------------------------------------------------------------------- */

    /**
     * 声明 fanout 交换机.
     * @return the exchange
     */
    @Bean("fanoutExchange")
    public FanoutExchange fanoutExchange() {
        return (FanoutExchange) ExchangeBuilder.fanoutExchange("FANOUT_EXCHANGE").durable(true).build();
    }

    /**
     * Fanout queue A.
     *
     * @return the queue
     */
    @Bean("fanoutQueueA")
    public Queue fanoutQueueA() {
        return QueueBuilder.durable("FANOUT_QUEUE_A").build();
    }

    /**
     * Fanout queue B .
     *
     * @return the queue
     */
    @Bean("fanoutQueueB")
    public Queue fanoutQueueB() {
        return QueueBuilder.durable("FANOUT_QUEUE_B").build();
    }

    /**
     * 绑定队列A 到Fanout 交换机.
     *
     * @param queue          the queue
     * @param fanoutExchange the fanout exchange
     * @return the binding
     */
    @Bean
    public Binding bindingA(@Qualifier("fanoutQueueA") Queue queue,
                            @Qualifier("fanoutExchange") FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    /**
     * 绑定队列B 到Fanout 交换机.
     *
     * @param queue          the queue
     * @param fanoutExchange the fanout exchange
     * @return the binding
     */
    @Bean
    public Binding bindingB(@Qualifier("fanoutQueueB") Queue queue,
                            @Qualifier("fanoutExchange") FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

	@Bean
    public Queue topicQueue() {
        return new Queue("topic");
    }

}
