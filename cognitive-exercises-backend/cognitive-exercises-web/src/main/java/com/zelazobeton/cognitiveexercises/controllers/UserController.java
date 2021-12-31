package com.zelazobeton.cognitiveexercises.controllers;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.security.Principal;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.zelazobeton.cognitiveexercises.model.EmailFormDto;
import com.zelazobeton.cognitiveexercises.model.PasswordFormDto;
import com.zelazobeton.cognitiveexercises.model.UserDto;
import com.zelazobeton.cognitiveexercises.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercises.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "/v1/user")
public class UserController extends ExceptionHandling {
    private final UserService userService;

    public UserController(ExceptionMessageService exceptionMessageService,
            UserService userService) {
        super(exceptionMessageService);
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

    @PostMapping(path = "/password", produces = { "application/json" })
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<HttpResponse> changePassword(Principal principal,
            @RequestBody PasswordFormDto passwordFormDto) {
        boolean passwordChanged = this.userService.changePassword(principal.getName(), passwordFormDto);
        if (passwordChanged) {
            String responseMsg = this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_PASSWORD_CHANGED_SUCCESSFULLY);
            return new ResponseEntity<>(new HttpResponse(OK, responseMsg), OK);
        }
        String responseMsg = this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_PASSWORD_IS_INCORRECT);
        return new ResponseEntity<>(new HttpResponse(NOT_ACCEPTABLE, responseMsg), NOT_ACCEPTABLE);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestBody EmailFormDto emailForm)
            throws MessagingException {
        this.userService.resetPassword(emailForm.getEmail());
        String responseMsg = this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_EMAIL_WITH_NEW_PASSWORD) + emailForm.getEmail();
        return new ResponseEntity<>(new HttpResponse(OK,  responseMsg), OK);
    }
}
