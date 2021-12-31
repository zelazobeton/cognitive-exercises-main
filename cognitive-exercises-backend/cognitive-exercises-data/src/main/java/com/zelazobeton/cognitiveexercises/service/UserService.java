package com.zelazobeton.cognitiveexercises.service;

import java.io.IOException;
import javax.mail.MessagingException;

import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.model.PasswordFormDto;

public interface UserService {
    User register(String username, String email)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, MessagingException, IOException;

    User updateUser(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException;

    User findUserByUsername(String username) throws UserNotFoundException;

    boolean changePassword(String username, PasswordFormDto passwordFormDto) throws UserNotFoundException;

    void resetPassword(String email) throws MessagingException;
}
