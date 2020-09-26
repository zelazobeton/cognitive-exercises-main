package com.zelazobeton.cognitiveexercises.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercises.exception.ExceptionHandling;

@RestController
@RequestMapping(value = "/user")
public class UserController extends ExceptionHandling {

    @GetMapping
    public String sampleEndpoint() {
        return "UserController works!";
    }
}
