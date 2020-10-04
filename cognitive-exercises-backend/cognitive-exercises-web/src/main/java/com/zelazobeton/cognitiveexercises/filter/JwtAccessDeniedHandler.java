package com.zelazobeton.cognitiveexercises.filter;

import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.ACCESS_DENIED_MSG;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final JwtServletRequestHandler jwtServletRequestHandler;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException {
        jwtServletRequestHandler.handle(request, response, UNAUTHORIZED, ACCESS_DENIED_MSG);
    }
}
