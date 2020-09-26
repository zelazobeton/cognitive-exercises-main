package com.zelazobeton.cognitiveexercises.exception.domain;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String message) {
        super(message);
    }
}
