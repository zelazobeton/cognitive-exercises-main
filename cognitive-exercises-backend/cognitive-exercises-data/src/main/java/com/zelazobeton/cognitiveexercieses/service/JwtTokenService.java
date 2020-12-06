package com.zelazobeton.cognitiveexercieses.service;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zelazobeton.cognitiveexercieses.domain.security.User;

public interface JwtTokenService {
    void deleteRefreshToken(String refreshToken) throws JWTVerificationException;

    HttpHeaders prepareLoginHeaders(User user);

    String getNewAccessToken(String receivedRefreshToken) throws JWTVerificationException;

    Authentication getAuthentication(String username, HttpServletRequest request,
            Collection<? extends GrantedAuthority> authorities);

    boolean isAccessTokenValid(String username, String token) throws JWTVerificationException;

    String getSubjectFromAccessToken(String token) throws JWTVerificationException;
}
