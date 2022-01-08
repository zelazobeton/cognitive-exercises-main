package com.zelazobeton.cognitiveexercises.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException() {}
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
