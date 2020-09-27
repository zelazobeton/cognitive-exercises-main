package com.zelazobeton.cognitiveexercises.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.EntityAlreadyExistsException;
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

    @GetMapping("/register")
    public String registerUser(@RequestBody User user) throws EntityAlreadyExistsException {
        return "UserController works!";
    }
}
