package com.zelazobeton.cognitiveexercieses.service.rabbitMQ.impl;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelazobeton.cognitiveexercieses.domain.messages.LoginMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginMessageListenerImpl implements MessageListener {

    @Override
    public void onMessage(Message messageReceived) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginMessage message = objectMapper.readValue(messageReceived.getBody(), LoginMessage.class);
            log.debug("Message received in LoginMessageListenerImpl: " + message.toString());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
