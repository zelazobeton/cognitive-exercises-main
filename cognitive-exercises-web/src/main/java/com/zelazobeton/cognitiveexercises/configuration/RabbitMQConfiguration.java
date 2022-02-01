package com.zelazobeton.cognitiveexercises.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Value("${spring.rabbitmq.saveScoreQueue}")
    String saveScoreQueue;

    @Value("${spring.rabbitmq.saveScoreRoutingKey}")
    String saveScoreRoutingKey;

    @Value("${spring.rabbitmq.defaultExchange}")
    String defaultExchange;

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(this.defaultExchange);
    }

    @Bean
    public Binding loginBinding(DirectExchange exchange, Queue loginQueue) {
        //        return BindingBuilder.bind(loginQueue).to(exchange).withQueueName();
        return BindingBuilder.bind(loginQueue).to(exchange).with(this.saveScoreRoutingKey);
    }

    @Bean
    Queue loginQueue() {
        return new Queue(this.saveScoreQueue, false);
    }
}
