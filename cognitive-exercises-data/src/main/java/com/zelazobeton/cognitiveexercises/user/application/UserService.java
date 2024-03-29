package com.zelazobeton.cognitiveexercises.user.application;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.mail.MessagingException;

import org.springframework.http.ResponseEntity;

import com.zelazobeton.cognitiveexercises.user.domain.User;
import com.zelazobeton.cognitiveexercises.shared.HttpResponse;

public interface UserService {
    User register(String username, String email)
            throws MessagingException, IOException;

    User updateUser(String externalId, String newUsername, String newEmail);

    User findUserByExternalId(String username);

    ResponseEntity<HttpResponse> changePassword(String externalId, PasswordFormDto passwordFormDto)
            throws ExecutionException, InterruptedException;

    void resetPassword(String email) throws MessagingException;
}
