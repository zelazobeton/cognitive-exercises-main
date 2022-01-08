package com.zelazobeton.cognitiveexercises.exception;

public class UsernameAlreadyExistsException extends EntityNotFoundException {
    public UsernameAlreadyExistsException(String message) {
        super("Username " + message + " already exists.");
    }
}
