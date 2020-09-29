package com.zelazobeton.cognitiveexercieses.service;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.UsernameAlreadyExistsException;

public interface UserService extends UserDetailsService {
    User register(String username, String email)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, MessagingException;

    User updateUser(String currentUsername, String newUsername, String newEmail, boolean isNonLocked, boolean isActive)
            throws UserNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException, IOException;

    User findUserByUsername(String username) throws UserNotFoundException;
}
