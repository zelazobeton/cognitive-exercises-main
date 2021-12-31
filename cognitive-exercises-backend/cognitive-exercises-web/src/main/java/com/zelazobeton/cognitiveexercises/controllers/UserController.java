package com.zelazobeton.cognitiveexercises.controllers;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.security.Principal;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.HttpResponse;
import com.zelazobeton.cognitiveexercises.constant.MessageConstants;
import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.model.PasswordFormDto;
import com.zelazobeton.cognitiveexercises.model.UserDto;
import com.zelazobeton.cognitiveexercises.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercises.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "/v1/user")
public class UserController extends ExceptionHandling {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public UserController(ExceptionMessageService exceptionMessageService, AuthenticationManager authenticationManager,
            UserService userService) {
        super(exceptionMessageService);
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping(path = "/register", produces = { "application/json" })
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto)
            throws MessagingException, IOException {
        User newUser = this.userService.register(userDto.getUsername(), userDto.getEmail());
        return new ResponseEntity<>(new UserDto(newUser), HttpStatus.OK);
    }

    @GetMapping(produces = { "application/json" })
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<UserDto> getUserData(Principal principal) {
        User userData = this.userService.findUserByUsername(principal.getName());
        return new ResponseEntity<>(new UserDto(userData), HttpStatus.OK);
    }

    @PostMapping(produces = { "application/json" })
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<UserDto> updateUser(Principal principal,
            @RequestParam("username") String username, @RequestParam("email") String email) {
        User updatedUser = this.userService.updateUser(principal.getName(), username, email);
        return new ResponseEntity<>(new UserDto(updatedUser), HttpStatus.OK);
    }

    @DeleteMapping
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<HttpResponse> deleteUser(Principal principal) {
        User user = this.userService.findUserByUsername(principal.getName());
        this.userService.deleteUser(user);
        String responseMsg = this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_USER_DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(new HttpResponse(OK, responseMsg), OK);
    }

    @PostMapping(path = "/password", produces = { "application/json" })
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<HttpResponse> changePassword(Principal principal,
            @RequestBody PasswordFormDto passwordFormDto) {
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(principal.getName(), passwordFormDto.getOldPassword()));
        }
        catch (AuthenticationException ex) {
            log.debug(ex.toString());
            String responseMsg = this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_PASSWORD_IS_INCORRECT);
            return new ResponseEntity<>(new HttpResponse(NOT_ACCEPTABLE, responseMsg), NOT_ACCEPTABLE);
        }
        this.userService.changePassword(principal.getName(), passwordFormDto.getNewPassword());
        String responseMsg = this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_PASSWORD_CHANGED_SUCCESSFULLY);
        return new ResponseEntity<>(new HttpResponse(OK, responseMsg), OK);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestParam("email") String email)
            throws MessagingException {
        this.userService.resetPassword(email);
        String responseMsg = this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_EMAIL_WITH_NEW_PASSWORD) + email;
        return new ResponseEntity<>(new HttpResponse(OK,  responseMsg), OK);
    }
}
