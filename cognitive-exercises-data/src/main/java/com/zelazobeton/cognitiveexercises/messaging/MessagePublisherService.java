package com.zelazobeton.cognitiveexercises.messaging;

import com.zelazobeton.cognitiveexercises.messaging.messages.AbstractQueueMessage;

public interface MessagePublisherService {
    void publishMessage(AbstractQueueMessage message, String exchange);

    void publishMessage(AbstractQueueMessage message);
}