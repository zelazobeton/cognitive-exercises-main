package com.zelazobeton.cognitiveexercises.service;

import com.zelazobeton.cognitiveexercises.model.AuthServerUserDto;

public interface AuthorizationServerService {
    AuthServerUserDto registerUserInAuthorizationServer(String username, String password, String email);

    boolean isPasswordCorrect(String username, String password);

    void setNewPasswordForUser(String authServerUserId, String username, String newPassword);
}
