package com.zelazobeton.cognitiveexercises.service.impl;

import java.util.Objects;
import java.util.UUID;

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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.zelazobeton.cognitiveexercises.model.AdminAccessTokenDto;
import com.zelazobeton.cognitiveexercises.model.AuthServerRoleDto;
import com.zelazobeton.cognitiveexercises.model.AuthServerUserDto;
import com.zelazobeton.cognitiveexercises.service.AuthorizationServerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KeycloakServiceImpl implements AuthorizationServerService {
    private RestTemplate restTemplate;
    @Value("${custom-keycloak-params.admin-access-token-url}")
    private String adminAccessTokenUrl;
    @Value("${custom-keycloak-params.admin-cli.secret}")
    private String registrationClientSecret;
    @Value("${custom-keycloak-params.admin-cli.id}")
    private String registrationClientId;
    @Value("${custom-keycloak-params.token-client.id}")
    private String tokenClientId;

    public KeycloakServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public boolean isPasswordCorrect(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", this.tokenClientId);
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = this.restTemplate.exchange(this.adminAccessTokenUrl,
                    HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                    }, String.class);
        } catch(HttpClientErrorException ex) {
            log.info(ex.toString());
        }

        return responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK;
    }

    @Override
    public void setNewPasswordForUser(String authServerUserId, String username, String newPassword) {
        String accessToken = this.getAuthorizationServerAdminAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = new JSONObject()
                .put("type", "password")
                .put("value", newPassword)
                .put("temporary", "false")
                .toString();
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String url = "http://localhost:8080/auth/admin/realms/cognitive-exercises/users/" + authServerUserId + "/reset-password";
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(url,
                HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
                }, AdminAccessTokenDto.class);

        if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new HttpClientErrorException(responseEntity.getStatusCode());
        }
    }

    @Override
    public AuthServerUserDto registerUserInAuthorizationServer(String username, String password, String email) {
        String adminAccessToken = this.getAuthorizationServerAdminAccessToken();
        this.saveNewUserInAuthorizationServer(username, password, email, adminAccessToken);
        AuthServerUserDto userDto = this.getUserDataFromAuthorizationServer(username, adminAccessToken);
        this.assignRolesInAuthorizationServer(userDto, adminAccessToken);
        return userDto;
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

    private String saveNewUserInAuthorizationServer(String username, String password, String email,
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
}
