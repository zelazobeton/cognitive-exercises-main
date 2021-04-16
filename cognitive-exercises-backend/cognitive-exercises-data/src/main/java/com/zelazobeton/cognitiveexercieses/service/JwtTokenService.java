package com.zelazobeton.cognitiveexercieses.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;

public interface JwtTokenService {
    void deleteRefreshTokenByUserId(Long userId) throws JWTVerificationException;

    void deleteRefreshTokenByRefreshToken(String refreshToken) throws JWTVerificationException;

    HttpHeaders prepareLoginHeaders(User user);

    String getNewAccessToken(String receivedRefreshToken) throws JWTVerificationException;

    Authentication getAuthentication(String token, HttpServletRequest request) throws JWTVerificationException,
            UserNotFoundException;
}
