package com.zelazobeton.cognitiveexercises.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.mail.MessagingException;

import org.springframework.http.ResponseEntity;

import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.model.HttpResponse;
import com.zelazobeton.cognitiveexercises.model.PasswordFormDto;

public interface UserService {
    User register(String username, String email)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, MessagingException, IOException;

    User updateUser(String externalId, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException;

    User findUserByExternalId(String username);

    ResponseEntity<HttpResponse> changePassword(String externalId, PasswordFormDto passwordFormDto)
            throws UserNotFoundException, ExecutionException, InterruptedException;

    void resetPassword(String email) throws MessagingException;
}
