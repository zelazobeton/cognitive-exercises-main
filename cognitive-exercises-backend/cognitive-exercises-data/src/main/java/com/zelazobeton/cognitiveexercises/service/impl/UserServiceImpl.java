package com.zelazobeton.cognitiveexercises.service.impl;

import static com.zelazobeton.cognitiveexercises.constant.RolesConstant.USER;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.mail.MessagingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercises.domain.security.Role;
import com.zelazobeton.cognitiveexercises.domain.security.User;
import com.zelazobeton.cognitiveexercises.domain.security.UserPrincipal;
import com.zelazobeton.cognitiveexercises.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.EmailNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.RegisterFormInvalidException;
import com.zelazobeton.cognitiveexercises.exception.RoleNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.repository.RoleRepository;
import com.zelazobeton.cognitiveexercises.repository.UserRepository;
import com.zelazobeton.cognitiveexercises.service.EmailService;
import com.zelazobeton.cognitiveexercises.service.UserService;
import com.zelazobeton.cognitiveexercises.service.impl.LoginAttemptServiceImpl;
import com.zelazobeton.cognitiveexercises.service.impl.PortfolioBuilderImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private LoginAttemptServiceImpl loginAttemptService;
    private EmailService emailService;
    private PortfolioBuilderImpl portfolioBuilderImpl;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder, LoginAttemptServiceImpl loginAttemptService,
            EmailService emailService, PortfolioBuilderImpl portfolioBuilderImpl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.portfolioBuilderImpl = portfolioBuilderImpl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Getting User info via JPA");

        User user = this.userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
        this.lockUserIfHasExceededMaxFailedLoginAttempts(user);
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        this.userRepository.save(user);

        return new UserPrincipal(user);
    }

    private void lockUserIfHasExceededMaxFailedLoginAttempts(User user) {
        if (user.isNotLocked()) {
            if (this.loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                log.debug(user.getUsername() + " has exceeded max number of login attempts");
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            this.loginAttemptService.removeUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String username, String email)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, MessagingException, IOException {
        this.validateNewUsernameAndEmail(username, email);
        String password = this.generatePassword();
        Role userRole = this.roleRepository.findByName(USER).orElseThrow(RoleNotFoundException::new);
        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(this.encodePassword(password))
                .role(userRole)
                .build();
        this.portfolioBuilderImpl.createPortfolioWithGeneratedAvatar(newUser);
        this.emailService.sendNewPasswordEmail(username, password, email);
        log.debug(username + " password: " + password);
        return this.userRepository.save(newUser);
    }

    @Override
    public User updateUser(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException {
        User currentUser = this.userRepository.findUserByUsername(currentUsername).orElseThrow(UserNotFoundException::new);
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
    public void changePassword(String username, String newPassword) throws UserNotFoundException {
        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setPassword(this.encodePassword(newPassword));
        this.userRepository.save(user);
    }

    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
        User user = this.userRepository.findUserByEmail(email).orElseThrow(EmailNotFoundException::new);
        String password = this.generatePassword();
        user.setPassword(this.encodePassword(password));
        this.userRepository.save(user);
        log.debug("New password: " + password);
        this.emailService.sendNewPasswordEmail(user.getUsername(), password, user.getEmail());
    }

    @Override
    public void deleteUser(User user) {
        this.userRepository.deleteById(user.getId());
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

    private String encodePassword(String password) {
        return this.bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}

