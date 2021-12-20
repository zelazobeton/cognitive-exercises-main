package com.zelazobeton.cognitiveexercises.exception;

public class EmailAlreadyExistsException extends EntityAlreadyExistsException {
    public EmailAlreadyExistsException(String email) {
        super("User with email " + email + " already exists.");
    }
}
