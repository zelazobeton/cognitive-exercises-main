package com.zelazobeton.cognitiveexercises.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.EntityAlreadyExistsException;
import com.zelazobeton.cognitiveexercieses.model.UserDto;
import com.zelazobeton.cognitiveexercieses.service.UserService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = {"/"})
public class UserController extends ExceptionHandling {

    private final UserService userService;

    @GetMapping
    public String sampleEndpoint() {
        return "UserController works!";
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) throws EntityAlreadyExistsException {
        User newUser = userService.register(userDto.getUsername(), userDto.getEmail());
        return new ResponseEntity<>(new UserDto(newUser), HttpStatus.OK);
    }
}
