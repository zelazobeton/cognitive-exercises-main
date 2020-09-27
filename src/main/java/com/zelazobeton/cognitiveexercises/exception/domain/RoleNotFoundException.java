package com.zelazobeton.cognitiveexercises.exception.domain;

public class RoleNotFoundException extends EntityNotFoundException {
    public RoleNotFoundException(String message) {
        super(message);
    }
    public RoleNotFoundException() {}
}
