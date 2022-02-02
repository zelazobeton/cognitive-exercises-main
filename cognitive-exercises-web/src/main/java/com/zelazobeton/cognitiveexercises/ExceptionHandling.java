package com.zelazobeton.cognitiveexercises;

import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_AN_ERROR_OCCURRED_WHILE_PROCESSING_THE_REQUEST;
import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_ERROR_OCCURRED_WHILE_PROCESSING_FILE;
import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_SUBMITTED_USERNAME_OR_EMAIL_WERE_INVALID;
import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_THIS_REQUEST_METHOD_IS_NOT_ALLOWED_ON_THIS_ENDPOINT;
import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_USERNAME_PASSWORD_INCORRECT;
import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_YOUR_ACCOUNT_HAS_BEEN_DISABLED;
import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_YOUR_ACCOUNT_HAS_BEEN_LOCKED;
import static com.zelazobeton.cognitiveexercises.constant.MessageConstants.EXCEPTION_HANDLING_YOU_DO_NOT_HAVE_THE_RIGHT_PERMISSION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;
import javax.persistence.NoResultException;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zelazobeton.cognitiveexercises.exception.EntityAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercises.exception.RegisterFormInvalidException;
import com.zelazobeton.cognitiveexercises.model.HttpResponse;
import com.zelazobeton.cognitiveexercises.service.ExceptionMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandling implements ErrorController {
    public static final String ERROR_PATH = "/error";
    protected final ExceptionMessageService exceptionMessageService;


    @ExceptionHandler(RegisterFormInvalidException.class)
    public ResponseEntity<HttpResponse> registerFormInvalidException() {
        return this.createHttpResponse(BAD_REQUEST, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_SUBMITTED_USERNAME_OR_EMAIL_WERE_INVALID));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return this.createHttpResponse(BAD_REQUEST, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_YOUR_ACCOUNT_HAS_BEEN_DISABLED));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return this.createHttpResponse(BAD_REQUEST, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_USERNAME_PASSWORD_INCORRECT));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpResponse> authenticationException(AuthenticationException e) {
        return this.createHttpResponse(BAD_REQUEST, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_USERNAME_PASSWORD_INCORRECT));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return this.createHttpResponse(UNAUTHORIZED, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_YOU_DO_NOT_HAVE_THE_RIGHT_PERMISSION));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return this.createHttpResponse(UNAUTHORIZED, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_YOUR_ACCOUNT_HAS_BEEN_LOCKED));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return this.createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<HttpResponse> usernameExistException(EntityAlreadyExistsException exception) {
        return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<HttpResponse> entityNotFoundException(EntityNotFoundException exception) {
        return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error(exception.getMessage());
        return this.createHttpResponse(METHOD_NOT_ALLOWED, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_THIS_REQUEST_METHOD_IS_NOT_ALLOWED_ON_THIS_ENDPOINT));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return this.createHttpResponse(INTERNAL_SERVER_ERROR, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_AN_ERROR_OCCURRED_WHILE_PROCESSING_THE_REQUEST));
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return this.createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return this.createHttpResponse(INTERNAL_SERVER_ERROR, this.exceptionMessageService.getMessage(EXCEPTION_HANDLING_ERROR_OCCURRED_WHILE_PROCESSING_FILE));
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception) {
        log.error(exception.getMessage());
        return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException e) {
        return this.createHttpResponse(BAD_REQUEST, "There is no mapping for this URL");
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus, message), httpStatus);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return this.createHttpResponse(NOT_FOUND, "There is no mapping for this URL");
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
