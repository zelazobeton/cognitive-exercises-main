package com.zelazobeton.cognitiveexercieses.service.rabbitMQ.impl;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercieses.domain.messages.AbstractQueueMessage;
import com.zelazobeton.cognitiveexercieses.exception.MessagePublishException;
import com.zelazobeton.cognitiveexercieses.service.rabbitMQ.MessagePublisherService;

@Service
public class MessagePublisherServiceImpl implements MessagePublisherService {

    @Value("${spring.rabbitmq.defaultExchange}")
    private String defaultExchangeName;
    private AmqpTemplate amqpTemplate;
    private Map<String, List<String>> routings;

    public MessagePublisherServiceImpl(AmqpTemplate amqpTemplate,
            @Value("#{${spring.rabbitmq.routings}}") Map<String, List<String>> routings) {
        this.amqpTemplate = amqpTemplate;
        this.routings = routings;
    }

    @Override
    @Transactional
    public void publishMessage(AbstractQueueMessage message, String exchange) {
        try {
            List<String> routingKeys = this.routings.get(message.key());
            if (routingKeys == null || routingKeys.isEmpty()) {
                throw new MessagePublishException(String.format(
                        "Error during sending the message to the exchange: %s - missing routing key for %s event",
                        exchange, message.key()));
            }
            for (String routingKey : routingKeys) {
                this.amqpTemplate.convertAndSend(exchange, routingKey, message);
            }
        } catch (AmqpException exception) {
            throw new MessagePublishException(
                    String.format("Error during sending the message to the exchange: %s", exchange), exception);
        }
    }

    @Override
    @Transactional
    public void publishMessage(AbstractQueueMessage message) {
        this.publishMessage(message, this.defaultExchangeName);
    }
}
