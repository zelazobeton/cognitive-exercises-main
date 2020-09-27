package com.zelazobeton.cognitiveexercises.bootstrap;

import static com.zelazobeton.cognitiveexercieses.constant.RolesConstant.*;

import java.util.Arrays;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercieses.domain.security.Authority;
import com.zelazobeton.cognitiveexercieses.domain.security.Role;
import com.zelazobeton.cognitiveexercieses.repository.AuthorityRepository;
import com.zelazobeton.cognitiveexercieses.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"dev-mysql-bootstrap"})
public class BootstrapDb implements CommandLineRunner {

    public static final String DUNEDIN_USER = "dunedin";

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        loadRoles();
    }

    private void loadRoles() {
        Authority createUser = authorityRepository.save(Authority.builder().permission("user.create").build());
        Authority updateUser = authorityRepository.save(Authority.builder().permission("user.update").build());
        Authority readUser = authorityRepository.save(Authority.builder().permission("user.read").build());
        Authority deleteUser = authorityRepository.save(Authority.builder().permission("user.delete").build());

        Role adminRole = Role.builder().name(ADMIN).build();
        Role userRole = Role.builder().name(USER).build();

        adminRole.setAuthorities(Set.of(createUser, updateUser, readUser, deleteUser));
        userRole.setAuthorities(Set.of(readUser));
        roleRepository.saveAll(Arrays.asList(adminRole, userRole));

        log.debug("Authorities Loaded: " + authorityRepository.count());
        log.debug("Roles Loaded: " + roleRepository.count());
    }
}
