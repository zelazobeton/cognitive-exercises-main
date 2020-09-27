package com.zelazobeton.cognitiveexercises.filter;

import static com.zelazobeton.cognitiveexercises.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.zelazobeton.cognitiveexercises.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zelazobeton.cognitiveexercises.utility.JWTTokenProvider;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private JWTTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticateRequest(request, authorizationHeader);
        }
        finally {
            filterChain.doFilter(request, response);
        }
    }

    private void authenticateRequest(HttpServletRequest request, String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        String username = jwtTokenProvider.getSubject(token);
        if (jwtTokenProvider.isTokenValid(username, token)) {
            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(token);
            Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }
    }

}
