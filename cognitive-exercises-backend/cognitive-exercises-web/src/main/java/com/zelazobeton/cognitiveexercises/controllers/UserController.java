package com.zelazobeton.cognitiveexercises.controllers;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import javax.mail.MessagingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.domain.messages.LoginMessage;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.model.PasswordFormDto;
import com.zelazobeton.cognitiveexercieses.model.UserDto;
import com.zelazobeton.cognitiveexercieses.service.rabbitMQ.MessagePublisherService;
import com.zelazobeton.cognitiveexercieses.service.impl.JwtTokenServiceImpl;
import com.zelazobeton.cognitiveexercieses.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercieses.service.UserService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.HttpResponse;
import com.zelazobeton.cognitiveexercises.constant.MessageConstants;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "/user")
public class UserController extends ExceptionHandling {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenServiceImpl jwtTokenProvider;
    private final MessagePublisherService messagePublisherService;

    public UserController(ExceptionMessageService exceptionMessageService, AuthenticationManager authenticationManager,
            UserService userService, JwtTokenServiceImpl jwtTokenProvider,
            MessagePublisherService messagePublisherService) {
        super(exceptionMessageService);
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messagePublisherService = messagePublisherService;
    }

    @PostMapping(path = "/register", produces = { "application/json" })
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto)
            throws MessagingException, IOException {
        User newUser = userService.register(userDto.getUsername(), userDto.getEmail());
        return new ResponseEntity<>(new UserDto(newUser), HttpStatus.OK);
    }

    @PostMapping(path = "/login", produces = { "application/json" })
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        User authenticatedUser = userService.findUserByUsername(userDto.getUsername());
        HttpHeaders loginHeaders = jwtTokenProvider.prepareLoginHeaders(authenticatedUser);
        messagePublisherService.publishMessage(new LoginMessage(authenticatedUser.getUsername()));
        return new ResponseEntity<>(new UserDto(authenticatedUser), loginHeaders, HttpStatus.OK);
    }

    @GetMapping(path = "/data", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<UserDto> getUserData(@AuthenticationPrincipal User user) {
        User userData = userService.findUserByUsername(user.getUsername());
        return new ResponseEntity<>(new UserDto(userData), HttpStatus.OK);
    }

    @PostMapping(path = "/change-password", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<HttpResponse> changePassword(@AuthenticationPrincipal User user,
            @RequestBody PasswordFormDto passwordFormDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), passwordFormDto.getOldPassword()));
        }
        catch (AuthenticationException ex) {
            log.debug(ex.toString());
            String responseMsg = exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_PASSWORD_IS_INCORRECT);
            return new ResponseEntity<>(new HttpResponse(NOT_ACCEPTABLE, responseMsg), NOT_ACCEPTABLE);
        }
        userService.changePassword(user.getUsername(), passwordFormDto.getNewPassword());
        String responseMsg = exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_PASSWORD_CHANGED_SUCCESSFULLY);
        return new ResponseEntity<>(new HttpResponse(OK, responseMsg), OK);
    }

    @PostMapping(path = "/update", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<UserDto> updateUser(@AuthenticationPrincipal User user,
            @RequestParam("username") String username, @RequestParam("email") String email) {
        User updatedUser = userService.updateUser(user.getUsername(), username, email);
        return new ResponseEntity<>(new UserDto(updatedUser), HttpStatus.OK);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestParam("email") String email)
            throws MessagingException {
        userService.resetPassword(email);
        String responseMsg = exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_EMAIL_WITH_NEW_PASSWORD) + email;
        return new ResponseEntity<>(new HttpResponse(OK,  responseMsg), OK);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('user.delete')")
    public ResponseEntity<HttpResponse> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        String responseMsg = exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_USER_DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(new HttpResponse(OK, responseMsg), OK);
    }

    @GetMapping(path = "/logout", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<HttpResponse> logout(@AuthenticationPrincipal User user) {
        jwtTokenProvider.deleteRefreshTokenByUserId(user.getId());
        return new ResponseEntity<>(
                new HttpResponse(OK, exceptionMessageService.getMessage(MessageConstants.TOKEN_CONTROLLER_REFRESH_TOKEN_SUCCESSFULLY_DELETED)),
                HttpStatus.OK);
    }
}
