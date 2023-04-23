package com.zelazobeton.cognitiveexercises.user.application;

import java.util.concurrent.Future;

import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.AuthServerUserDto;

public interface AuthorizationServerService {
    AuthServerUserDto registerUserInAuthorizationServer(String username, String password, String email);

    Future<Boolean> isPasswordCorrect(String username, String password);

    void setNewPasswordForUser(String authServerUserId, String username, String newPassword, String accessToken);

    void setNewPasswordForUser(String authServerUserId, String username, String newPassword);

    Future<String> getAuthorizationServerAdminAccessTokenAsync();
}
