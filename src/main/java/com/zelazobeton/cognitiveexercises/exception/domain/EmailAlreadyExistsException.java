package com.zelazobeton.cognitiveexercises.exception.domain;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
