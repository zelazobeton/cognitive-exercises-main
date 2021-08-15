package com.zelazobeton.cognitiveexercieses.exception;

public class MessagePublishException extends EntityNotFoundException {
    public MessagePublishException(String message) {
        super(message);
    }

    public MessagePublishException() {
    }

    public MessagePublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
