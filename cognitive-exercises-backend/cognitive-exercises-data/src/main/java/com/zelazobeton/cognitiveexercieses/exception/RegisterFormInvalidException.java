package com.zelazobeton.cognitiveexercieses.exception;

public class RegisterFormInvalidException extends EmailNotFoundException {
    public RegisterFormInvalidException(String message) {
        super(message);
    }
}
