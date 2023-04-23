package com.zelazobeton.cognitiveexercises.user.adapters.out.authorization;

import java.util.Objects;
import java.util.concurrent.Future;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.AuthServerRoleDto;
import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.AuthServerUserDto;
import com.zelazobeton.cognitiveexercises.user.application.AuthorizationServerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class KeycloakServiceImpl implements AuthorizationServerService {
    private RestTemplate restTemplate;
    @Value("${custom-keycloak-params.admin-access-token-url}")
    private String adminAccessTokenUrl;
    @Value("${custom-keycloak-params.admin-roles-url}")
    private String adminRolesUrl;
    @Value("${custom-keycloak-params.role-mapping-url}")
    private String roleMappingUrl;
    @Value("${custom-keycloak-params.reset-password-url}")
    private String resetPasswordUrl;
    @Value("${custom-keycloak-params.get-user-data-url}")
    private String getUserDataUrl;
    @Value("${custom-keycloak-params.register-user-url}")
    private String registerUserUrl;
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
    @Async
    public Future<Boolean> isPasswordCorrect(String username, String password) {
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
        return new AsyncResult<>(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK);
    }

    @Override
    public void setNewPasswordForUser(String authServerUserId, String username, String newPassword, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = new JSONObject()
                .put("type", "password")
                .put("value", newPassword)
                .put("temporary", "false")
                .toString();
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String resetPasswordUrlWithUserId = this.resetPasswordUrl.replaceAll("\\{\\{userId}}", authServerUserId);

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(resetPasswordUrlWithUserId,
                HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
                }, AdminAccessTokenDto.class);

        if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new HttpClientErrorException(responseEntity.getStatusCode());
        }
    }

    @Override
    public void setNewPasswordForUser(String authServerUserId, String username, String newPassword) {
        String accessToken = this.getAuthorizationServerAdminAccessToken();
        this.setNewPasswordForUser(authServerUserId, username, newPassword, accessToken);
    }

    @Override
    public AuthServerUserDto registerUserInAuthorizationServer(String username, String password, String email) {
        String adminAccessToken = this.getAuthorizationServerAdminAccessToken();
        this.saveNewUserInAuthorizationServer(username, password, email, adminAccessToken);
        AuthServerUserDto userDto = this.getUserDataFromAuthorizationServer(username, adminAccessToken);
        this.assignRolesInAuthorizationServer(userDto, adminAccessToken);
        return userDto;
    }

    @Override
    @Async
    public Future<String> getAuthorizationServerAdminAccessTokenAsync() {
        return new AsyncResult<>(this.getAuthorizationServerAdminAccessToken());
    }

    private AuthServerUserDto getUserDataFromAuthorizationServer(String username, String adminAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminAccessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String getUserDataUrlWithUserId = this.getUserDataUrl.replaceAll("\\{\\{username}}", username);

        ResponseEntity<AuthServerUserDto[]> getUserDataResponse = this.restTemplate.exchange(
                getUserDataUrlWithUserId,
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

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.registerUserUrl, HttpMethod.POST, entity,
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
        ResponseEntity<AuthServerRoleDto[]> getRolesResponse = this.restTemplate.exchange(this.adminRolesUrl,
                HttpMethod.GET, entity, AuthServerRoleDto[].class);

        AuthServerRoleDto[] roleDtos = Objects.requireNonNull(getRolesResponse.getBody());
        String roleId = null;
        for (AuthServerRoleDto roleDto : roleDtos) {
            if (roleDto.getName().equals("app-user")) {
                roleId = roleDto.getId().toString();
            }
        }

        String userId = userDto.getId().toString();
        JSONObject realmRole = new JSONObject()
                .put("name", "app-user")
                .put("id", roleId);
        String realmRolesArray = (new JSONArray()).put(realmRole).toString();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> setRolesEntity = new HttpEntity<>(realmRolesArray, headers);
        String roleMappingUrlWithUserId = this.roleMappingUrl.replaceAll("\\{\\{userId}}", userId);

        ResponseEntity<String> setRolesResponse = this.restTemplate.exchange(
                roleMappingUrlWithUserId,
                HttpMethod.POST, setRolesEntity, String.class);

        if (setRolesResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RuntimeException("Roles has not been set for new user on authorization server.");
        }
    }
}
