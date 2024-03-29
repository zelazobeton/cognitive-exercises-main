package com.zelazobeton.cognitiveexercises.messaging;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercises.exception.MessagePublishException;
import com.zelazobeton.cognitiveexercises.messaging.messages.AbstractQueueMessage;

@Service
class MessagePublisherServiceImpl implements MessagePublisherService {

    @Value("${spring.rabbitmq.defaultExchange}")
    private String defaultExchangeName;
    private RabbitTemplate rabbitTemplate;
    private Map<String, List<String>> routings;

    public MessagePublisherServiceImpl(RabbitTemplate rabbitTemplate,
            @Value("#{${spring.rabbitmq.routings}}") Map<String, List<String>> routings) {
        this.rabbitTemplate = rabbitTemplate;
        this.routings = routings;
    }

    @Override
    public void publishMessage(AbstractQueueMessage message, String exchange) {
        try {
            List<String> routingKeys = this.routings.get(message.key());
            if (routingKeys == null || routingKeys.isEmpty()) {
                throw new MessagePublishException(String.format(
                        "Error during sending the message to the exchange: %s - missing routing key for %s event",
                        exchange, message.key()));
            }
            for (String routingKey : routingKeys) {
                this.rabbitTemplate.convertAndSend(exchange, routingKey, message);
            }
        } catch (AmqpException exception) {
            throw new MessagePublishException(
                    String.format("Error during sending the message to the exchange: %s", exchange), exception);
        }
    }

    @Override
    public void publishMessage(AbstractQueueMessage message) {
        this.publishMessage(message, this.defaultExchangeName);
    }
}
