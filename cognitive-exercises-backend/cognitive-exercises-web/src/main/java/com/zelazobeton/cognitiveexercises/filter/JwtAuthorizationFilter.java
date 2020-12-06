package com.zelazobeton.cognitiveexercises.filter;

import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.OPTIONS_HTTP_METHOD;
import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zelazobeton.cognitiveexercieses.domain.security.Role;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.repository.UserRepository;
import com.zelazobeton.cognitiveexercises.utility.JWTTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, JWTVerificationException {
        try {
            tryAuthenticateRequest(request, response);
        }
        catch (JWTVerificationException ex) {
            log.debug(ex.toString());
        }
        finally {
            filterChain.doFilter(request, response);
        }
    }

    private void tryAuthenticateRequest(HttpServletRequest request, HttpServletResponse response) {
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
            JWTVerificationException {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        String username = jwtTokenProvider.getSubjectFromAccessToken(token);
        if (jwtTokenProvider.isAccessTokenValid(username, token)) {
            Set<? extends GrantedAuthority> authorities = getUserAuthorities(username);
            Authentication authentication = jwtTokenProvider.getAuthentication(username, request, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }
    }

    private Set<? extends GrantedAuthority> getUserAuthorities(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        return user.getRoles().stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .map(auth -> new SimpleGrantedAuthority(auth.getPermission()))
                .collect(Collectors.toSet());
    }

}
