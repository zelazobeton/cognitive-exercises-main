package com.zelazobeton.cognitiveexercises.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.zelazobeton.cognitiveexercises.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtServletRequestHandler {
    public void handle(HttpServletRequest request, HttpServletResponse response, HttpStatus httpStatus, String message) throws
            IOException {
        HttpResponse httpResponse = new HttpResponse(httpStatus, message);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
