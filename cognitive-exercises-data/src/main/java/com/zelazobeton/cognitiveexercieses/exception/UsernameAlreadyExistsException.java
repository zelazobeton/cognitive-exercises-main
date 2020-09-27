package com.zelazobeton.cognitiveexercieses.exception;

public class UsernameAlreadyExistsException extends EmailNotFoundException {
    public UsernameAlreadyExistsException(String message) {
        super("Username " + message + " already exists.");
    }
}
