package com.zelazobeton.cognitiveexercises.email.application;

import javax.mail.MessagingException;

public interface EmailService {
    void sendNewPasswordEmail(String username, String password, String email) throws MessagingException;
    boolean validateEmail(String email);
}
