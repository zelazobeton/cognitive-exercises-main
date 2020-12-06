package com.zelazobeton.cognitiveexercieses.service;

import static com.zelazobeton.cognitiveexercieses.constant.TokenConstant.JWT_REFRESH_TOKEN_HEADER;
import static com.zelazobeton.cognitiveexercieses.constant.TokenConstant.JWT_TOKEN_HEADER;
import static com.zelazobeton.cognitiveexercieses.constant.TokenConstant.REFRESH_TOKEN_EXPIRATION_TIME;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zelazobeton.cognitiveexercieses.constant.TokenConstant;
import com.zelazobeton.cognitiveexercieses.domain.security.RefreshToken;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.repository.RefreshTokenRepository;
import com.zelazobeton.cognitiveexercieses.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService{

    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void deleteRefreshToken(String refreshToken) throws JWTVerificationException {
        JWTVerifier verifier = getJWTVerifier();
        String jwtId = verifier.verify(refreshToken).getId();
        refreshTokenRepository.deleteById(UUID.fromString(jwtId));
    }

    @Override
    public HttpHeaders prepareLoginHeaders(User user) {
        HttpHeaders headers = new HttpHeaders();
        refreshTokenRepository.deleteByUser_Id(user.getId());
        headers.add(JWT_TOKEN_HEADER, generateJwtToken(user.getUsername()));
        headers.add(JWT_REFRESH_TOKEN_HEADER, generateRefreshToken(user));
        headers.add("Access-Control-Expose-Headers", JWT_REFRESH_TOKEN_HEADER);
        return headers;
    }

    @Override
    public String getNewAccessToken(String receivedRefreshToken) throws JWTVerificationException {
        String username = getUsernameFromStoredRefreshToken(receivedRefreshToken);
        return generateJwtToken(username);
    }

    @Override
    public Authentication getAuthentication(String username, HttpServletRequest request,
            Collection<? extends GrantedAuthority> authorities) {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        UsernamePasswordAuthenticationToken userPasswordAuthToken =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    @Override
    public boolean isAccessTokenValid(String username, String token) throws JWTVerificationException{
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isAccessTokenExpired(verifier, token);
    }

    @Override
    public String getSubjectFromAccessToken(String token) throws JWTVerificationException{
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private String getUsernameFromStoredRefreshToken(String receivedRefreshToken)
            throws JWTVerificationException {
        JWTVerifier verifier = getJWTVerifier();
        String jwtId = verifier.verify(receivedRefreshToken).getId();

        if (StringUtils.isEmpty(jwtId)) {
            throw new JWTVerificationException("Refresh Token is invalid");
        }

        RefreshToken refreshToken = refreshTokenRepository.findById(UUID.fromString(jwtId))
                .orElseThrow(() -> new JWTVerificationException("Refresh token data not found in db"));

        if (refreshToken.getValidUntil().before(new Date())) {
            refreshTokenRepository.deleteById(UUID.fromString(jwtId));
            throw new JWTVerificationException("Refresh Token has expired");
        }
        return refreshToken.getUser().getUsername();
    }

    private String generateRefreshToken(User user) {
        Date validUntil = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);
        RefreshToken refreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .validUntil(validUntil)
                        .user(user)
                        .build());
        return JWT.create()
                .withIssuer(TokenConstant.TOKEN_ISSUER)
                .withJWTId(refreshToken.getId().toString())
                .withExpiresAt(validUntil)
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    private boolean isAccessTokenExpired(JWTVerifier verifier, String token) throws JWTVerificationException{
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private JWTVerifier getJWTVerifier() {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            return JWT.require(algorithm).withIssuer(TokenConstant.TOKEN_ISSUER).build();
        }
        catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TokenConstant.TOKEN_CANNOT_BE_VERIFIED_MSG);
        }
    }

    private String generateJwtToken(String username) {
        return JWT.create().withIssuer(TokenConstant.TOKEN_ISSUER)
                .withIssuedAt(new Date())
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + TokenConstant.ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }
}
