package com.zelazobeton.cognitiveexercises.service;

import static com.zelazobeton.cognitiveexercises.constant.RolesConstant.USER;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercises.domain.security.Role;
import com.zelazobeton.cognitiveexercises.domain.security.User;
import com.zelazobeton.cognitiveexercises.domain.security.UserPrincipal;
import com.zelazobeton.cognitiveexercises.exception.domain.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.domain.RoleNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.domain.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.domain.UsernameAlreadyExistException;
import com.zelazobeton.cognitiveexercises.repository.RoleRepository;
import com.zelazobeton.cognitiveexercises.repository.UserRepository;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Getting User info via JPA");

        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username: " + username + " not found"));
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        userRepository.save(user);

        return new UserPrincipal(user);
    }

    @Override
    public User register(String username, String email) throws UsernameAlreadyExistException, EmailAlreadyExistsException {
        validateNewUsernameAndEmail(username, email);
        String password = generatePassword();
        Role userRole = roleRepository.findByName(USER).orElseThrow(RoleNotFoundException::new);
        User newUser = User.builder().username(username).email(email).password(encodePassword(password)).role(userRole).build();
        log.debug("password: " + password);
        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(String currentUsername, String newUsername, String newEmail, boolean isNonLocked,
            boolean isActive)
            throws UserNotFoundException, UsernameAlreadyExistException, EmailAlreadyExistsException, IOException {
        User currentUser = userRepository.findUserByUsername(currentUsername).orElseThrow(UserNotFoundException::new);
        validateUpdatedUsernameAndEmail(newUsername, newEmail, currentUser.getId());
        currentUser.setEmail(newEmail);
        currentUser.setUsername(newUsername);
        return userRepository.save(currentUser);
    }

    private void validateNewUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistException(username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void validateUpdatedUsernameAndEmail(String username, String email, Long currentUserId) {
        Optional<User> userByNewUsername = userRepository.findUserByUsername(username);
        if (userByNewUsername.isPresent() && userByNewUsername.get().getId().equals(currentUserId)) {
            throw new UsernameAlreadyExistException(username);
        }
        Optional<User> userByNewEmail = userRepository.findUserByEmail(email);
        if (userByNewEmail.isPresent() && userByNewEmail.get().getId().equals(currentUserId)) {
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
