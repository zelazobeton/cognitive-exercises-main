package com.zelazobeton.cognitiveexercises.service;

public interface LoginAttemptService {
    void removeUserFromLoginAttemptCache(String username);
    void addUserToLoginAttemptCache(String username);
    boolean hasExceededMaxAttempts(String username);
}
