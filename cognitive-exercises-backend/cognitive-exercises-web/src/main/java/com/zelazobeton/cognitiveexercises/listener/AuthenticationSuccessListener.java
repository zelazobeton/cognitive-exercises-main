package com.zelazobeton.cognitiveexercises.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercises.domain.security.UserPrincipal;
import com.zelazobeton.cognitiveexercises.service.impl.LoginAttemptServiceImpl;

@Component
public class AuthenticationSuccessListener {
    private LoginAttemptServiceImpl loginAttemptService;

    public AuthenticationSuccessListener(LoginAttemptServiceImpl loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) principal;
            this.loginAttemptService.removeUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
