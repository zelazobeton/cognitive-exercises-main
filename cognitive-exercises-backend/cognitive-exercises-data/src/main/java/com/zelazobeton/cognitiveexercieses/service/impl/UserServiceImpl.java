package com.zelazobeton.cognitiveexercieses.service.impl;

import static com.zelazobeton.cognitiveexercieses.constant.RolesConstant.USER;

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

import com.zelazobeton.cognitiveexercieses.domain.security.Role;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.domain.security.UserPrincipal;
import com.zelazobeton.cognitiveexercieses.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercieses.exception.EmailNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.RegisterFormInvalidException;
import com.zelazobeton.cognitiveexercieses.exception.RoleNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercieses.repository.RoleRepository;
import com.zelazobeton.cognitiveexercieses.repository.UserRepository;
import com.zelazobeton.cognitiveexercieses.service.EmailService;
import com.zelazobeton.cognitiveexercieses.service.UserService;
import com.zelazobeton.cognitiveexercieses.service.impl.LoginAttemptServiceImpl;
import com.zelazobeton.cognitiveexercieses.service.impl.PortfolioBuilderImpl;

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

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
        lockUserIfHasExceededMaxFailedLoginAttempts(user);
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        userRepository.save(user);

        return new UserPrincipal(user);
    }

    private void lockUserIfHasExceededMaxFailedLoginAttempts(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                log.debug(user.getUsername() + " has exceeded max number of login attempts");
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.removeUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String username, String email)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, MessagingException, IOException {
        validateNewUsernameAndEmail(username, email);
        String password = generatePassword();
        Role userRole = roleRepository.findByName(USER).orElseThrow(RoleNotFoundException::new);
        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(encodePassword(password))
                .role(userRole)
                .build();
        portfolioBuilderImpl.createPortfolioWithGeneratedAvatar(newUser);
        emailService.sendNewPasswordEmail(username, password, email);
        log.debug(username + " password: " + password);
        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException {
        User currentUser = userRepository.findUserByUsername(currentUsername).orElseThrow(UserNotFoundException::new);
        setNewUsername(currentUser, newUsername);
        setNewEmail(currentUser, newEmail);
        return userRepository.save(currentUser);
    }

    @Override
    public User findUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    private void validateNewUsernameAndEmail(String username, String email) {
        if (username.length() >= 50 || !Pattern.matches("^[a-zA-Z0-9@.]+$", username)) {
            throw new RegisterFormInvalidException(username + " " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    @Override
    public void changePassword(String username, String newPassword) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setPassword(encodePassword(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(EmailNotFoundException::new);
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        log.debug("New password: " + password);
        emailService.sendNewPasswordEmail(user.getUsername(), password, user.getEmail());
    }

    @Override
    public void deleteUser(User user) {
        userRepository.deleteById(user.getId());
    }

    private void setNewEmail(User currentUser, String newEmail)
            throws UsernameAlreadyExistsException, IllegalArgumentException {
        if (newEmail == null || newEmail.equals("")) {
            return;
        }
        if (!emailService.validateEmail(newEmail)) {
            throw new IllegalArgumentException(newEmail + " is not a valid email address.");
        }
        Optional<User> userByNewEmail = userRepository.findUserByEmail(newEmail);
        if (userByNewEmail.isPresent() && !userByNewEmail.get().getId().equals(currentUser.getId())) {
            throw new EmailAlreadyExistsException(newEmail);
        }
        currentUser.setEmail(newEmail);
    }

    private void setNewUsername(User currentUser, String newUsername) throws UsernameAlreadyExistsException {
        if (newUsername == null || newUsername.equals("")) {
            return;
        }
        Optional<User> userByNewUsername = userRepository.findUserByUsername(newUsername);
        if (userByNewUsername.isPresent() && !userByNewUsername.get().getId().equals(currentUser.getId())) {
            throw new UsernameAlreadyExistsException(newUsername);
        }
        currentUser.setUsername(newUsername);
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}

