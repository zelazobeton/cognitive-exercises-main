package com.zelazobeton.cognitiveexercises.filter;

import static com.zelazobeton.cognitiveexercieses.constant.TokenConstant.TOKEN_PREFIX;
import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.OPTIONS_HTTP_METHOD;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.service.JwtTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenServiceImpl;

    public JwtAuthorizationFilter(JwtTokenService jwtTokenServiceImpl) {
        this.jwtTokenServiceImpl = jwtTokenServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, JWTVerificationException {
        try {
            tryAuthenticateRequest(request, response);
        }
        catch (JWTVerificationException | UserNotFoundException ex) {
            log.debug(ex.toString());
        }
        finally {
            filterChain.doFilter(request, response);
        }
    }

    private void tryAuthenticateRequest(HttpServletRequest request, HttpServletResponse response) throws
            JWTVerificationException, UserNotFoundException {
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            SecurityContextHolder.clearContext();
            return;
        }
        authenticateRequest(request, authorizationHeader);
    }

    private void authenticateRequest(HttpServletRequest request, String authorizationHeader) throws
            JWTVerificationException, UserNotFoundException {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        Authentication authentication = jwtTokenServiceImpl.getAuthentication(token, request);
        if (authentication == null) {
            SecurityContextHolder.clearContext();
        } else {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
