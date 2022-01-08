package com.zelazobeton.cognitiveexercises.service.impl;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.mail.MessagingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.EmailNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.RegisterFormInvalidException;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.model.AuthServerUserDto;
import com.zelazobeton.cognitiveexercises.model.PasswordFormDto;
import com.zelazobeton.cognitiveexercises.repository.UserRepository;
import com.zelazobeton.cognitiveexercises.service.AuthorizationServerService;
import com.zelazobeton.cognitiveexercises.service.EmailService;
import com.zelazobeton.cognitiveexercises.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private EmailService emailService;
    private PortfolioBuilderImpl portfolioBuilderImpl;
    private AuthorizationServerService authorizationServerService;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService,
            PortfolioBuilderImpl portfolioBuilderImpl, AuthorizationServerService keycloakServiceImpl) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.portfolioBuilderImpl = portfolioBuilderImpl;
        this.authorizationServerService = keycloakServiceImpl;
    }

    @Override
    public User register(String username, String email)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, MessagingException, IOException {
        this.validateNewUsernameAndEmail(username, email);
        String password = this.generatePassword();
        AuthServerUserDto userDto = this.authorizationServerService.registerUserInAuthorizationServer(username,
                password, email);

        String authServerUserId = userDto.getId().toString();
        User newUser = User.builder().username(username).email(email).externalId(authServerUserId).build();
        this.portfolioBuilderImpl.createPortfolioWithGeneratedAvatar(newUser);
        this.emailService.sendNewPasswordEmail(username, password, email);
        log.debug(username + " password: " + password);
        return this.userRepository.save(newUser);
    }

    @Override
    public User updateUser(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException {
        User currentUser = this.userRepository.findUserByUsername(currentUsername)
                .orElseThrow(UserNotFoundException::new);
        this.setNewUsername(currentUser, newUsername);
        this.setNewEmail(currentUser, newEmail);
        return this.userRepository.save(currentUser);
    }

    @Override
    public User findUserByUsername(String username) throws UserNotFoundException {
        return this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    private void validateNewUsernameAndEmail(String username, String email) {
        if (username.length() >= 50 || !Pattern.matches("^[a-zA-Z0-9@.]+$", username)) {
            throw new RegisterFormInvalidException(username + " " + email);
        }
        if (this.userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
        if (this.userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    @Override
    public boolean changePassword(String username, PasswordFormDto passwordFormDto) throws UserNotFoundException {
        if (!this.authorizationServerService.isPasswordCorrect(username, passwordFormDto.getOldPassword())) {
            return false;
        }
        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        this.authorizationServerService.setNewPasswordForUser(user.getExternalId(), username,
                passwordFormDto.getNewPassword());
        return true;
    }

    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
        User user = this.userRepository.findUserByEmail(email).orElseThrow(EmailNotFoundException::new);
        String password = this.generatePassword();
        this.emailService.sendNewPasswordEmail(user.getUsername(), password, email);
        this.authorizationServerService.setNewPasswordForUser(user.getExternalId(), user.getUsername(),
                password);
    }

    private void setNewEmail(User currentUser, String newEmail)
            throws UsernameAlreadyExistsException, IllegalArgumentException {
        if (newEmail == null || newEmail.equals("")) {
            return;
        }
        if (!this.emailService.validateEmail(newEmail)) {
            throw new IllegalArgumentException(newEmail + " is not a valid email address.");
        }
        Optional<User> userByNewEmail = this.userRepository.findUserByEmail(newEmail);
        if (userByNewEmail.isPresent() && !userByNewEmail.get().getId().equals(currentUser.getId())) {
            throw new EmailAlreadyExistsException(newEmail);
        }
        currentUser.setEmail(newEmail);
    }

    private void setNewUsername(User currentUser, String newUsername) throws UsernameAlreadyExistsException {
        if (newUsername == null || newUsername.equals("")) {
            return;
        }
        Optional<User> userByNewUsername = this.userRepository.findUserByUsername(newUsername);
        if (userByNewUsername.isPresent() && !userByNewUsername.get().getId().equals(currentUser.getId())) {
            throw new UsernameAlreadyExistsException(newUsername);
        }
        currentUser.setUsername(newUsername);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
