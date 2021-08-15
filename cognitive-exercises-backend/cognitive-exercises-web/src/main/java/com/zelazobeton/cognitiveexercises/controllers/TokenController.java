package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercieses.constant.TokenConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercieses.service.impl.JwtTokenServiceImpl;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.HttpResponse;
import com.zelazobeton.cognitiveexercises.constant.MessageConstants;

@RestController
@RequestMapping(path = "/token")
public class TokenController extends ExceptionHandling {
    private final JwtTokenServiceImpl jwtTokenProvider;

    public TokenController(ExceptionMessageService exceptionMessageService, JwtTokenServiceImpl jwtTokenProvider) {
        super(exceptionMessageService);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(path = "/refresh", produces = { "application/json" })
    public ResponseEntity<HttpResponse> getNewAccessToken(@RequestBody String refreshToken) {
        String token = this.jwtTokenProvider.getNewAccessToken(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, token);
        return new ResponseEntity<>(
                new HttpResponse(OK, this.exceptionMessageService.getMessage(MessageConstants.TOKEN_CONTROLLER_TOKEN_SUCCESSFULLY_REFRESHED)),
                headers, HttpStatus.OK);
    }

    @PostMapping(path = "/delete", produces = { "application/json" })
    public ResponseEntity<HttpResponse> deleteRefreshToken(@RequestBody String refreshToken) {
        this.jwtTokenProvider.deleteRefreshTokenByRefreshToken(refreshToken);
        return new ResponseEntity<>(
                new HttpResponse(OK, this.exceptionMessageService.getMessage(MessageConstants.TOKEN_CONTROLLER_REFRESH_TOKEN_SUCCESSFULLY_DELETED)),
                HttpStatus.OK);
    }
}
