package com.zelazobeton.cognitiveexercises.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zelazobeton.cognitiveexercises.service.rabbitMQ.impl.LoginMessageListenerImpl;

@Configuration
public class LoginMessageListenerContainer {

    private Queue loginQueue;

    public LoginMessageListenerContainer(Queue loginQueue) {
        this.loginQueue = loginQueue;
    }

    @Bean
    MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.setQueues(this.loginQueue);
        simpleMessageListenerContainer.setMessageListener(new LoginMessageListenerImpl());
        return simpleMessageListenerContainer;
    }
}
