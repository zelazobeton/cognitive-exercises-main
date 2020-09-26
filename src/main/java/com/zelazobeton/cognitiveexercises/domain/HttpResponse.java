package com.zelazobeton.cognitiveexercises.domain;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class HttpResponse {
    private HttpStatus httpStatus;
    private String message;
}
