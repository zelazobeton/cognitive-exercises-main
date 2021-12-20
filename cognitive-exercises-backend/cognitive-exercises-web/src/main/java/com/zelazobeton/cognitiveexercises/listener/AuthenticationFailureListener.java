package com.zelazobeton.cognitiveexercises.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercises.service.impl.LoginAttemptServiceImpl;

@Component
public class AuthenticationFailureListener {
    private LoginAttemptServiceImpl loginAttemptService;

    public AuthenticationFailureListener(LoginAttemptServiceImpl loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String) {
            this.loginAttemptService.addUserToLoginAttemptCache((String) principal);
        }

    }
}
