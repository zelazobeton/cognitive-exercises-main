package com.zelazobeton.cognitiveexercises.domain.messages;

import java.io.Serializable;

public class LoginMessage extends AbstractQueueMessage implements Serializable {

    private static final long serialVersionUID = 8282399521931183336L;
    private String username;

    public LoginMessage() {
    }

    public LoginMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "\"" + this.username + " has just logged in\"";
    }
}
