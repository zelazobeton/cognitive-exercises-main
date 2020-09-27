package com.zelazobeton.cognitiveexercises.exception.domain;

public class EmailNotFoundException extends EntityNotFoundException {
    public EmailNotFoundException(String message) {
        super(message);
    }
}
