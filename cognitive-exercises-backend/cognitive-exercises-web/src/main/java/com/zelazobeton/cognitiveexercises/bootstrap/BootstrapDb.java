package com.zelazobeton.cognitiveexercises.bootstrap;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.MEMORY_IMG_FOLDER;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.MEMORY_IMG_PATH;
import static com.zelazobeton.cognitiveexercieses.constant.RolesConstant.ADMIN;
import static com.zelazobeton.cognitiveexercieses.constant.RolesConstant.USER;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryImg;
import com.zelazobeton.cognitiveexercieses.domain.security.Authority;
import com.zelazobeton.cognitiveexercieses.domain.security.Role;
import com.zelazobeton.cognitiveexercieses.repository.AuthorityRepository;
import com.zelazobeton.cognitiveexercieses.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercieses.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"dev-mysql-bootstrap"})
public class BootstrapDb implements CommandLineRunner {
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final MemoryImgRepository memoryImgRepository;

    @Override
    public void run(String... args) {
//        loadRoles();
        loadMemoryImages();
    }

    private void loadMemoryImages() {
        Path memoryImagesFolder = Paths.get(MEMORY_IMG_FOLDER).toAbsolutePath().normalize();
        List<MemoryImg> memoryImgs = new ArrayList<>();
        for(File file: memoryImagesFolder.toFile().listFiles()) {
            if (!file.isDirectory()) {
                String imgAddress = "http://localhost:8081" + MEMORY_IMG_PATH + file.getName();
                memoryImgs.add(MemoryImg.builder().address(imgAddress).build());
            }
        }
        memoryImgRepository.saveAll(memoryImgs);
    }

    private void loadRoles() {
        Authority createUser = authorityRepository.save(Authority.builder().permission("user.create").build());
        Authority updateUser = authorityRepository.save(Authority.builder().permission("user.update").build());
        Authority readUser = authorityRepository.save(Authority.builder().permission("user.read").build());
        Authority deleteUser = authorityRepository.save(Authority.builder().permission("user.delete").build());

        Role adminRole = Role.builder().name(ADMIN).build();
        Role userRole = Role.builder().name(USER).build();

        adminRole.setAuthorities(Set.of(createUser, updateUser, readUser, deleteUser));
        userRole.setAuthorities(Set.of(readUser, updateUser, deleteUser));
        roleRepository.saveAll(Arrays.asList(adminRole, userRole));

        log.debug("Authorities Loaded: " + authorityRepository.count());
        log.debug("Roles Loaded: " + roleRepository.count());
    }
}
