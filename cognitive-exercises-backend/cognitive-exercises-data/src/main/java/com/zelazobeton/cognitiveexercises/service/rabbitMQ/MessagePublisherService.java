package com.zelazobeton.cognitiveexercises.service.rabbitMQ;

import com.zelazobeton.cognitiveexercises.domain.messages.AbstractQueueMessage;

public interface MessagePublisherService {
    void publishMessage(AbstractQueueMessage message, String exchange);
    void publishMessage(AbstractQueueMessage message);
}
