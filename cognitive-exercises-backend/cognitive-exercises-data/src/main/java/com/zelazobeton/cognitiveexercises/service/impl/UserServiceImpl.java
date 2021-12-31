package com.zelazobeton.cognitiveexercises.service.impl;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.mail.MessagingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.zelazobeton.cognitiveexercises.domain.security.User;
import com.zelazobeton.cognitiveexercises.domain.security.UserPrincipal;
import com.zelazobeton.cognitiveexercises.exception.EmailAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.exception.EmailNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.RegisterFormInvalidException;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.UsernameAlreadyExistsException;
import com.zelazobeton.cognitiveexercises.model.AdminAccessTokenDto;
import com.zelazobeton.cognitiveexercises.model.AuthServerRoleDto;
import com.zelazobeton.cognitiveexercises.model.AuthServerUserDto;
import com.zelazobeton.cognitiveexercises.repository.UserRepository;
import com.zelazobeton.cognitiveexercises.service.EmailService;
import com.zelazobeton.cognitiveexercises.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private LoginAttemptServiceImpl loginAttemptService;
    private EmailService emailService;
    private PortfolioBuilderImpl portfolioBuilderImpl;
    private RestTemplate restTemplate;
    @Value("${custom-keycloak-params.admin-access-token-url}")
    private String adminAccessTokenUrl;
    @Value("${custom-keycloak-params.admin-cli.secret}")
    private String registrationClientSecret;
    @Value("${custom-keycloak-params.admin-cli.id}")
    private String registrationClientId;

    public UserServiceImpl(UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder, LoginAttemptServiceImpl loginAttemptService,
            EmailService emailService, PortfolioBuilderImpl portfolioBuilderImpl,
            RestTemplateBuilder restTemplateBuilder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.portfolioBuilderImpl = portfolioBuilderImpl;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Getting User info via JPA");

        User user = this.userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
//        this.lockUserIfHasExceededMaxFailedLoginAttempts(user);
//        user.setLastLoginDateDisplay(user.getLastLoginDate());
//        user.setLastLoginDate(new Date());
        this.userRepository.save(user);

        return new UserPrincipal(user);
    }

//    private void lockUserIfHasExceededMaxFailedLoginAttempts(User user) {
//        if (user.isNotLocked()) {
//            if (this.loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
//                log.debug(user.getUsername() + " has exceeded max number of login attempts");
//                user.setNotLocked(false);
//            } else {
//                user.setNotLocked(true);
//            }
//        } else {
//            this.loginAttemptService.removeUserFromLoginAttemptCache(user.getUsername());
//        }
//    }

    private String getAuthorizationServerAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", this.registrationClientId);
        map.add("client_secret", this.registrationClientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<AdminAccessTokenDto> responseEntity = this.restTemplate.exchange(this.adminAccessTokenUrl,
                HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                }, AdminAccessTokenDto.class);

        AdminAccessTokenDto adminAccessTokenDto = Objects.requireNonNull(responseEntity.getBody());
        return adminAccessTokenDto.getAccess_token();
    }

    private String registerUserInAuthorizationServer(String username, String password, String email,
            String adminAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject credentials = new JSONObject()
                .put("type", "password")
                .put("value", password)
                .put("temporary", "false");
        String jsonObject = new JSONObject()
                .put("email", email)
                .put("username", username)
                .put("enabled", "true")
                .put("emailVerified", "true")
                .put("credentials", new JSONArray().put(credentials))
                .toString();
        HttpEntity<String> entity = new HttpEntity<>(jsonObject, headers);

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                "http://localhost:8080/auth/admin/realms/cognitive-exercises/users", HttpMethod.POST, entity,
                new ParameterizedTypeReference<>() {
                }, AdminAccessTokenDto.class);
        return responseEntity.getBody();
    }

    private AuthServerUserDto getUserDataFromAuthorizationServer(String username, String adminAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminAccessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthServerUserDto[]> getUserDataResponse = this.restTemplate.exchange(
                "http://localhost:8080/auth/admin/realms/cognitive-exercises/users/?username=" + username,
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                }, AuthServerUserDto[].class);

        AuthServerUserDto[] userDtos = Objects.requireNonNull(getUserDataResponse.getBody());
        if (userDtos.length == 0) {
            throw new RuntimeException("User with given username does not exist in authorization server.");
        }

        if (userDtos.length > 1) {
            throw new RuntimeException("There is more than 1 user with given username on authorization server.");
        }
        return userDtos[0];
    }

    private void assignRolesInAuthorizationServer(AuthServerUserDto userDto, String adminAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminAccessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthServerRoleDto[]> getRolesResponse = this.restTemplate.exchange(
                "http://localhost:8080/auth/admin/realms/cognitive-exercises/roles",
                HttpMethod.GET, entity, AuthServerRoleDto[].class);

        AuthServerRoleDto[] roleDtos = Objects.requireNonNull(getRolesResponse.getBody());
        String roleId = null;
        for (AuthServerRoleDto roleDto : roleDtos) {
            if (roleDto.getName().equals("app-user")) {
                roleId = roleDto.getId().toString();
            }
        }

        UUID userId = userDto.getId();
        JSONObject realmRole = new JSONObject()
                .put("name", "app-user")
                .put("id", roleId);
        String realmRolesArray = (new JSONArray()).put(realmRole).toString();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> setRolesEntity = new HttpEntity<>(realmRolesArray, headers);
        ResponseEntity<String> setRolesResponse = this.restTemplate.exchange(
                "http://localhost:8080/auth/admin/realms/cognitive-exercises/users/" + userId + "/role-mappings/realm",
                HttpMethod.POST, setRolesEntity, String.class);

        if (setRolesResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RuntimeException("Roles has not been set for new user on authorization server.");
        }
    }

    @Override
    public User register(String username, String email)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, MessagingException, IOException {
        this.validateNewUsernameAndEmail(username, email);
        String adminAccessToken = this.getAuthorizationServerAdminAccessToken();
        String password = this.generatePassword();
        this.registerUserInAuthorizationServer(username, password, email, adminAccessToken);
        AuthServerUserDto userDto = this.getUserDataFromAuthorizationServer(username, adminAccessToken);
        this.assignRolesInAuthorizationServer(userDto, adminAccessToken);

        String authServerUserId = userDto.getId().toString();
        User newUser = User.builder().username(username).email(email).authServerId(authServerUserId).build();
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

