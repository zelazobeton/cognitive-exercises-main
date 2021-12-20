package com.zelazobeton.cognitiveexercises.exception;

public class RegisterFormInvalidException extends EmailNotFoundException {
    public RegisterFormInvalidException(String message) {
        super(message);
    }
}
