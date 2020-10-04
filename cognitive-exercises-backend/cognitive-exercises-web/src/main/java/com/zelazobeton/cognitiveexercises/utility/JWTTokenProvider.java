package com.zelazobeton.cognitiveexercises.utility;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.EXPIRATION_TIME;
import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.TOKEN_CANNOT_BE_VERIFIED_MSG;
import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.TOKEN_ISSUER;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.domain.security.UserPrincipal;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    public String generateJwtToken(UserPrincipal userPrincipal) {
        return JWT.create().withIssuer(TOKEN_ISSUER)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    public Authentication getAuthentication(String username, HttpServletRequest request,
            Collection<? extends GrantedAuthority> authorities) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        UsernamePasswordAuthenticationToken userPasswordAuthToken =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public boolean isTokenValid(String username, String token) throws JWTVerificationException{
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token) throws JWTVerificationException{
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) throws JWTVerificationException{
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private JWTVerifier getJWTVerifier() {
        try {
            Algorithm algorithm = HMAC512(secret);
            return JWT.require(algorithm).withIssuer(TOKEN_ISSUER).build();
        }
        catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED_MSG);
        }
    }
}
