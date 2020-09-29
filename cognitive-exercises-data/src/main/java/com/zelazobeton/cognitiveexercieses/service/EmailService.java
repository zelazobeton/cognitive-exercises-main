package com.zelazobeton.cognitiveexercieses.service;

public interface EmailService {
    void sendNewPasswordEmail(String username, String password, String email);
}
