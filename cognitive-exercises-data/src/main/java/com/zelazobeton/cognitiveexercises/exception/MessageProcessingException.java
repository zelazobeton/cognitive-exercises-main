package com.zelazobeton.cognitiveexercises.exception;

import org.springframework.amqp.core.Message;

public class MessageProcessingException extends RuntimeException {
    public Message queueMessage;

    public MessageProcessingException(String message) {
        super(message);
    }

    public MessageProcessingException(String message, Message queueMessage) {
        super(message);
        this.queueMessage = queueMessage;
    }

    public MessageProcessingException() {
    }

    public MessageProcessingException(String message, Throwable cause, Message queueMessage) {
        super(message, cause);
        this.queueMessage = queueMessage;
    }
}
