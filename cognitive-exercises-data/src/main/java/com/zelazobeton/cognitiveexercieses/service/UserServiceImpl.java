package com.zelazobeton.cognitiveexercieses.service;

import static com.zelazobeton.cognitiveexercieses.constant.RolesConstant.USER;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

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
import com.zelazobeton.cognitiveexercieses.exception.RoleNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercieses.repository.RoleRepository;
import com.zelazobeton.cognitiveexercieses.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptServiceImpl loginAttemptService;
    private final EmailServiceImpl emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Getting User info via JPA");

        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username: " + username + " not found"));
        lockUserIfHasExceededMaxFailedLoginAttempts(user);
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        userRepository.save(user);

        return new UserPrincipal(user);
    }

    private void lockUserIfHasExceededMaxFailedLoginAttempts(User user) {
        if(user.isNotLocked()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
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
    public User register(String username, String email) throws UsernameAlreadyExistsException, EmailAlreadyExistsException,
            MessagingException {
        validateNewUsernameAndEmail(username, email);
        String password = generatePassword();
        Role userRole = roleRepository.findByName(USER).orElseThrow(RoleNotFoundException::new);
        User newUser = User.builder().username(username).email(email).password(encodePassword(password)).role(userRole).build();
        emailService.sendNewPasswordEmail(username, password, email);
        log.debug(username + " password: " + password);
        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(String currentUsername, String newUsername, String newEmail, boolean isNonLocked,
            boolean isActive)
            throws UserNotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException, IOException {
        User currentUser = userRepository.findUserByUsername(currentUsername).orElseThrow(UserNotFoundException::new);
        validateUpdatedUsernameAndEmail(newUsername, newEmail, currentUser.getId());
        currentUser.setEmail(newEmail);
        currentUser.setUsername(newUsername);
        return userRepository.save(currentUser);
    }

    @Override
    public User findUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    private void validateNewUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void validateUpdatedUsernameAndEmail(String username, String email, Long currentUserId) {
        Optional<User> userByNewUsername = userRepository.findUserByUsername(username);
        if (userByNewUsername.isPresent() && !userByNewUsername.get().getId().equals(currentUserId)) {
            throw new UsernameAlreadyExistsException(username);
        }
        Optional<User> userByNewEmail = userRepository.findUserByEmail(email);
        if (userByNewEmail.isPresent() && !userByNewEmail.get().getId().equals(currentUserId)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}

