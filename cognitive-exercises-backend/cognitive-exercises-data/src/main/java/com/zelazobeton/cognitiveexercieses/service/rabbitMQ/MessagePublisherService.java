package com.zelazobeton.cognitiveexercieses.service.rabbitMQ;

import com.zelazobeton.cognitiveexercieses.domain.messages.AbstractQueueMessage;

public interface MessagePublisherService {
    void publishMessage(AbstractQueueMessage message, String exchange);
    void publishMessage(AbstractQueueMessage message);
}
