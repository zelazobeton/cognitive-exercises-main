package com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthServerRoleDto {
    private Map<String, String> attributes;
    private boolean clientRole;
    private boolean composite;
    private Composite[] composites;
    private String containerId;
    private String description;
    private UUID id;
    private String name;
}

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Composite {
    private Map<String, String> client;
    private String[] realm;
}