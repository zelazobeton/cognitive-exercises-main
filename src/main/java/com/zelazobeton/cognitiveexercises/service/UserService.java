package com.zelazobeton.cognitiveexercises.service;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.zelazobeton.cognitiveexercises.domain.security.User;
import com.zelazobeton.cognitiveexercises.exception.domain.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.domain.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.domain.UsernameAlreadyExistException;

public interface UserService extends UserDetailsService {
    User register(String username, String email)
            throws UsernameAlreadyExistException, EmailAlreadyExistsException;

    User updateUser(String currentUsername, String newUsername, String newEmail, boolean isNonLocked, boolean isActive)
            throws UserNotFoundException, UsernameAlreadyExistException, EmailAlreadyExistsException, IOException;
}
