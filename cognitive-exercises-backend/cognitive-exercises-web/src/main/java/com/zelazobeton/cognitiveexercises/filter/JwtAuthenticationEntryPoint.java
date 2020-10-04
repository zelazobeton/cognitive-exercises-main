package com.zelazobeton.cognitiveexercises.filter;

import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.FORBIDDEN_MSG;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    private final JwtServletRequestHandler jwtServletRequestHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        jwtServletRequestHandler.handle(request, response, FORBIDDEN, FORBIDDEN_MSG);
    }
}
