package com.zelazobeton.cognitiveexercises.service.impl;

import static com.zelazobeton.cognitiveexercises.constant.FileConstants.AVATAR;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.DEFAULT_AVATAR_FILE;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.DEFAULT_AVATAR_FILENAME;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.DIRECTORY_CREATED;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.LOCALHOST_ADDRESS;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.USER_FOLDER;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.USER_IMAGE_PATH;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.VERSION_1;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zelazobeton.cognitiveexercises.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.domain.security.User;
import com.zelazobeton.cognitiveexercises.service.PortfolioBuilder;
import com.zelazobeton.cognitiveexercises.service.ResourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PortfolioBuilderImpl implements PortfolioBuilder {
    private ResourceService resourceService;

    public PortfolioBuilderImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Portfolio createPortfolioWithGeneratedAvatar(User user) throws IOException {
        return new Portfolio(user, this.generateAvatarAddress(user.getUsername()), 0L);
    }

    @Override
    public Portfolio createBootstrapPortfolioWithGeneratedAvatar(User user) throws IOException {
        return new Portfolio(user, this.generateAvatarAddressForBootstrapUsers(user.getUsername()), 0L);
    }

    private String generateAvatarAddressForBootstrapUsers(String username) throws IOException {
        this.generateAvatar(username);
        return LOCALHOST_ADDRESS + VERSION_1 + USER_IMAGE_PATH + username + FORWARD_SLASH + DEFAULT_AVATAR_FILENAME;
    }

    private String generateAvatarAddress(String username) throws IOException {
        this.generateAvatar(username);
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(VERSION_1 + USER_IMAGE_PATH + username + FORWARD_SLASH + DEFAULT_AVATAR_FILENAME)
                .toUriString();
    }

    private void generateAvatar(String username) throws IOException {
        Path target = this.resourceService.getPath(USER_FOLDER + FORWARD_SLASH + username + AVATAR);
        try {
            createFolderIfThereIsNone(target);
            URL website = new URL(generateRoboHashAddress());
            InputStream in = website.openStream();
            Files.copy(in, target.resolve(DEFAULT_AVATAR_FILENAME), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.info(ex.toString());
            Path defaultAvatarSrc = this.resourceService.getPath(DEFAULT_AVATAR_FILE);
            Files.copy(defaultAvatarSrc, target.resolve(DEFAULT_AVATAR_FILENAME), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String generateRoboHashAddress() {
        return "https://robohash.org/" + RandomStringUtils.randomAlphanumeric(10);
    }

    private static void createFolderIfThereIsNone(Path target) throws IOException {
        if(!Files.exists(target)) {
            Files.createDirectories(target);
            log.debug(DIRECTORY_CREATED + target);
        }
    }
}
