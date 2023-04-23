package com.zelazobeton.cognitiveexercises.user.application;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import javax.mail.MessagingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercises.exception.MessageConstants;
import com.zelazobeton.cognitiveexercises.user.domain.User;
import com.zelazobeton.cognitiveexercises.email.application.EmailService;
import com.zelazobeton.cognitiveexercises.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.EmailNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.RegisterFormInvalidException;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.AuthServerUserDto;
import com.zelazobeton.cognitiveexercises.shared.HttpResponse;
import com.zelazobeton.cognitiveexercises.exception.ExceptionMessageService;
import com.zelazobeton.cognitiveexercises.user.domain.PortfolioBuilderImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
class UserServiceImpl implements UserService {
    private EmailService emailService;
    private PortfolioBuilderImpl portfolioBuilder;
    private AuthorizationServerService authorizationServerService;
    private ExceptionMessageService exceptionMessageService;
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService,
            PortfolioBuilderImpl portfolioBuilder, AuthorizationServerService authorizationServerService,
            ExceptionMessageService exceptionMessageService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.portfolioBuilder = portfolioBuilder;
        this.authorizationServerService = authorizationServerService;
        this.exceptionMessageService = exceptionMessageService;
    }

    @Override
    public User register(String username, String email) throws IOException {
        this.validateNewUsernameAndEmail(username, email);
        String password = this.generatePassword();
        AuthServerUserDto userDto = this.authorizationServerService.registerUserInAuthorizationServer(username,
                password, email);

        String authServerUserId = userDto.getId().toString();
        User newUser = User.builder().username(username).email(email).externalId(authServerUserId).build();
        this.portfolioBuilder.createPortfolioWithGeneratedAvatar(newUser);
        //        this.emailService.sendNewPasswordEmail(username, password, email);
        log.debug(username + " password: " + password);
        return this.userRepository.save(newUser);
    }

    @Override
    public User updateUser(String externalId, String newUsername, String newEmail) {
        User currentUser = this.userRepository.findUserByExternalId(externalId).orElseThrow(() ->
            new UserNotFoundException("User with externalId: " + externalId + " does not exist"));
        this.setNewUsername(currentUser, newUsername);
        this.setNewEmail(currentUser, newEmail);
        return this.userRepository.save(currentUser);
    }

    @Override
    public User findUserByExternalId(String externalId) {
        Optional<User> user = this.userRepository.findUserByExternalId(externalId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with externalId: " + externalId + " does not exist");
        }
        return user.get();
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
    public ResponseEntity<HttpResponse> changePassword(String externalId, PasswordFormDto passwordFormDto)
            throws ExecutionException, InterruptedException {
        User user = this.userRepository.findUserByExternalId(externalId).orElseThrow(UserNotFoundException::new);
        Future<Boolean> isPasswordCorrect = this.authorizationServerService.isPasswordCorrect(user.getUsername(),
                passwordFormDto.getOldPassword());
        Future<String> adminAccessToken = this.authorizationServerService.getAuthorizationServerAdminAccessTokenAsync();

        if (!isPasswordCorrect.get()) {
            adminAccessToken.cancel(true);
            String responseMsg = this.exceptionMessageService.getMessage(
                    MessageConstants.USER_CONTROLLER_PASSWORD_IS_INCORRECT);
            return new ResponseEntity<>(new HttpResponse(NOT_ACCEPTABLE, responseMsg), NOT_ACCEPTABLE);
        }
        this.authorizationServerService.setNewPasswordForUser(externalId, user.getUsername(),
                passwordFormDto.getNewPassword(), adminAccessToken.get());
        String responseMsg = this.exceptionMessageService.getMessage(
                MessageConstants.USER_CONTROLLER_PASSWORD_CHANGED_SUCCESSFULLY);
        return new ResponseEntity<>(new HttpResponse(OK, responseMsg), OK);
    }

    @Override
    public void resetPassword(String email) throws MessagingException {
        User user = this.userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(
                        this.exceptionMessageService.getMessage(MessageConstants.USER_CONTROLLER_NO_SUCH_EMAIL)));
        String password = this.generatePassword();
        this.emailService.sendNewPasswordEmail(user.getUsername(), password, email);
        this.authorizationServerService.setNewPasswordForUser(user.getExternalId(), user.getUsername(), password);
    }

    private void setNewEmail(User currentUser, String newEmail) {
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

    private void setNewUsername(User currentUser, String newUsername) {
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
