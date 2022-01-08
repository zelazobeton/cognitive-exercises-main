package com.zelazobeton.cognitiveexercises;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpResponse {
    private HttpStatus httpStatus;
    private String message;

    public HttpResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
