package com.zelazobeton.cognitiveexercises.exception;

public class EmailNotFoundException extends EntityNotFoundException {
    public EmailNotFoundException(String message) {
        super(message);
    }
    public EmailNotFoundException() { super(); }
}
