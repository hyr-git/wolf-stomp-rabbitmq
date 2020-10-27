package com.naah.stomp.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

	public final static String TO_WEB_QUEUE_NAME = "toWeb";
	public final static String TO_APP_QUEUE_NAME = "toApp";
	
	@Bean
	public Queue toWebQueue() {
		return new Queue(TO_WEB_QUEUE_NAME);
	}
	
	@Bean
	public Queue toAppQueue() {
		return new Queue(TO_APP_QUEUE_NAME);
	}
	
	     
	     /**
	      * 解决方法:添加这个类进行序列化解析
	      * 会自动识别
	      * @param objectMapper json序列化实现类
	      * @return mq 消息序列化工具
	      */
	     @Bean
	     public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
	         return new Jackson2JsonMessageConverter(objectMapper);
	     }
	
	     /**
	      * 连接工厂
	     
	     @Autowired
	     private ConnectionFactory connectionFactory; */
	     
	   /* @Bean
	    public RabbitTemplate rabbitTemplate() {
	        RabbitTemplate template = new RabbitTemplate();
	        template.setConnectionFactory(connectionFactory);
	        template.setMessageConverter(new Jackson2JsonMessageConverter());
	        return template;
	    }
	    
	    @Bean
	    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
	        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
	        factory.setConnectionFactory(connectionFactory);
	        factory.setMessageConverter(new Jackson2JsonMessageConverter());        
	        return factory;
	    }*/

	 
	  /*  @Bean
	    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
	        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
	        factory.setConnectionFactory(connectionFactory);
	        factory.setMessageConverter(new Jackson2JsonMessageConverter());
	        return factory;
	    }*/

}
