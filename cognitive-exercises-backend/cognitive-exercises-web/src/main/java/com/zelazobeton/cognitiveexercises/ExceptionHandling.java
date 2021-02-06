package com.zelazobeton.cognitiveexercises;

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
import com.zelazobeton.cognitiveexercieses.exception.EmailNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.EntityAlreadyExistsException;
import com.zelazobeton.cognitiveexercieses.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercieses.exception.RegisterFormInvalidException;
import com.zelazobeton.cognitiveexercieses.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandling implements ErrorController {
    public static final String ERROR_PATH = "/error";
    protected final MessageService messageService;


    @ExceptionHandler(RegisterFormInvalidException.class)
    public ResponseEntity<HttpResponse> registerFormInvalidException() {
        return createHttpResponse(BAD_REQUEST,
                messageService.getMessage("exception_handling_submitted_username_or_email_were_invalid"));
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException() {
        return createHttpResponse(BAD_REQUEST,
                messageService.getMessage("exception_handling_there_is_no_registered_user_with_that_email"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(BAD_REQUEST,
                messageService.getMessage("exception_handling_your_account_has_been_disabled"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST,
                messageService.getMessage("exception_handling_username_password_incorrect"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpResponse> authenticationException(AuthenticationException e) {
        return createHttpResponse(BAD_REQUEST,
                messageService.getMessage("exception_handling_username_password_incorrect"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(UNAUTHORIZED,
                messageService.getMessage("exception_handling_you_do_not_have_the_right_permission"));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED,
                messageService.getMessage("exception_handling_your_account_has_been_locked"));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<HttpResponse> usernameExistException(EntityAlreadyExistsException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<HttpResponse> entityNotFoundException(EntityNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(METHOD_NOT_ALLOWED,
                messageService.getMessage("exception_handling_this_request_method_is_not_allowed_on_this_endpoint"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR,
                messageService.getMessage("exception_handling_an_error_occurred_while_processing_the_request"));
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR,
                messageService.getMessage("exception_handling_error_occurred_while_processing_file"));
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException e) {
        return createHttpResponse(BAD_REQUEST, "There is no mapping for this URL");
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus, message), httpStatus);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, "There is no mapping for this URL");
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
