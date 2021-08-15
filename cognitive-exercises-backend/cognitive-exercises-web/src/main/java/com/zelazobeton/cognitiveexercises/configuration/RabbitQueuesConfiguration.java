package com.zelazobeton.cognitiveexercises.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueuesConfiguration {

    @Value("${spring.rabbitmq.loginQueue}")
    String loginQueue;

    @Value("${spring.rabbitmq.loginRoutingKey}")
    String loginRoutingKey;

    @Value("${spring.rabbitmq.defaultExchange}")
    String defaultExchange;

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(defaultExchange);
    }

    @Bean
    public Binding loginBinding(DirectExchange exchange, Queue loginQueue) {
        return BindingBuilder.bind(loginQueue).to(exchange).with(loginRoutingKey);
    }

    @Bean
    Queue loginQueue(DirectExchange exchange) {
        return new Queue(loginQueue, false);
    }
}
