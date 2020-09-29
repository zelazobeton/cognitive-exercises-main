package com.zelazobeton.cognitiveexercises.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercieses.domain.security.UserPrincipal;
import com.zelazobeton.cognitiveexercieses.service.LoginAttemptService;

@Component
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.removeUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
