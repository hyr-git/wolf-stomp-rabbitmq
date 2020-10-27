package com.naah.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	@Bean
    public Queue topicQueue() {
        return new Queue("topic");
    }

}
