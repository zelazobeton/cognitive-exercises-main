package com.zelazobeton.cognitiveexercises.controllers;

import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.EmailNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.EntityAlreadyExistsException;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.model.PasswordFormDto;
import com.zelazobeton.cognitiveexercieses.model.UserDto;
import com.zelazobeton.cognitiveexercieses.service.UserService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.HttpResponse;
import com.zelazobeton.cognitiveexercises.utility.JWTTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
public class UserController extends ExceptionHandling {
    public static final String EMAIL_WITH_PASSWORD_SENT = "Email with new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password changed successfully";

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping(path = "/register", produces = { "application/json" })
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto)
            throws EntityAlreadyExistsException, MessagingException, IOException {
        User newUser = userService.register(userDto.getUsername(), userDto.getEmail());
        return new ResponseEntity<>(new UserDto(newUser), HttpStatus.OK);
    }

    @PostMapping(path = "/login", produces = { "application/json" })
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto)
            throws AuthenticationException, UserNotFoundException {
        authenticate(userDto.getUsername(), userDto.getPassword());
        User authenticatedUser = userService.findUserByUsername(userDto.getUsername());
        HttpHeaders loginHeaders = jwtTokenProvider.prepareLoginHeaders(authenticatedUser);
        return new ResponseEntity<>(new UserDto(authenticatedUser), loginHeaders, HttpStatus.OK);
    }

    @GetMapping(path = "/data", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<UserDto> getUserData(@AuthenticationPrincipal User user) throws UserNotFoundException {
        User userData = userService.findUserByUsername(user.getUsername());
        return new ResponseEntity<>(new UserDto(userData), HttpStatus.OK);
    }

    @PostMapping(path = "/change-password", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<HttpResponse> changePassword(@AuthenticationPrincipal User user,
            @RequestBody PasswordFormDto passwordFormDto) {
        authenticate(user.getUsername(), passwordFormDto.getOldPassword());
        userService.changePassword(user.getUsername(), passwordFormDto.getNewPassword());
        return new ResponseEntity<>(new HttpResponse(OK, PASSWORD_CHANGED_SUCCESSFULLY), OK);
    }

    @PostMapping(path = "/update", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<UserDto> updateUser(@AuthenticationPrincipal User user,
            @RequestParam("username") String username, @RequestParam("email") String email)
            throws UserNotFoundException, EntityAlreadyExistsException {
        User updatedUser = userService.updateUser(user.getUsername(), username, email);
        return new ResponseEntity<>(new UserDto(updatedUser), HttpStatus.OK);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestParam("email") String email)
            throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return new ResponseEntity<>(new HttpResponse(OK, EMAIL_WITH_PASSWORD_SENT + email), OK);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('user.delete')")
    public ResponseEntity<HttpResponse> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return new ResponseEntity<>(new HttpResponse(OK, USER_DELETED_SUCCESSFULLY), OK);
    }

    private void authenticate(String username, String password) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
