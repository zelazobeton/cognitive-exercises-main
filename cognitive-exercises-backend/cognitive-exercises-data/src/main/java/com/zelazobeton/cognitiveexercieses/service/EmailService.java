package com.zelazobeton.cognitiveexercieses.service;

import javax.mail.MessagingException;

public interface EmailService {
    void sendNewPasswordEmail(String username, String password, String email) throws MessagingException;
}
