package com.zelazobeton.cognitiveexercises.service.impl;

import static com.zelazobeton.cognitiveexercises.constant.FileConstants.AVATAR;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.DIRECTORY_CREATED;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.NOT_AN_IMAGE_FILE;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.USER_FOLDER;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.USER_IMAGE_PATH;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.VERSION_1;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zelazobeton.cognitiveexercises.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercises.model.PortfolioDto;
import com.zelazobeton.cognitiveexercises.model.ScoreboardPageDto;
import com.zelazobeton.cognitiveexercises.model.UserScoreDto;
import com.zelazobeton.cognitiveexercises.repository.PortfolioRepository;
import com.zelazobeton.cognitiveexercises.repository.UserRepository;
import com.zelazobeton.cognitiveexercises.service.PortfolioService;
import com.zelazobeton.cognitiveexercises.service.ResourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {
    private UserRepository userRepository;
    private PortfolioRepository portfolioRepository;
    private ResourceService resourceService;

    public PortfolioServiceImpl(UserRepository userRepository, PortfolioRepository portfolioRepository,
            ResourceService resourceService) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.resourceService = resourceService;
    }

    @Override
    public PortfolioDto updateAvatar(String username, MultipartFile avatar)
            throws EntityNotFoundException, IOException, NotAnImageFileException {
        User currentUser = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        Portfolio currentPortfolio = currentUser.getPortfolio();
        if (avatar != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(avatar.getContentType())) {
                throw new NotAnImageFileException(avatar.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }

            Path userAvatarFolder = this.resourceService.getPath(USER_FOLDER + FORWARD_SLASH + currentUser.getUsername() + AVATAR);
            if(!Files.exists(userAvatarFolder)) {
                Files.createDirectories(userAvatarFolder);
                log.debug(DIRECTORY_CREATED + userAvatarFolder);
            }

            for(File file: Objects.requireNonNull(userAvatarFolder.toFile().listFiles())) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
            String filename = avatar.getOriginalFilename() == null ? "avatar.jpg" : avatar.getOriginalFilename();
            Files.copy(avatar.getInputStream(), userAvatarFolder.resolve(filename), REPLACE_EXISTING);
            currentPortfolio.setAvatar(this.createProfileImageUrl(currentUser.getUsername(), avatar.getOriginalFilename()));
        }
        return new PortfolioDto(this.portfolioRepository.save(currentPortfolio));
    }

    @Override
    public ScoreboardPageDto getScoreboardPage(int pageNumber, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("totalScore").descending());
        Page<Portfolio> page = this.portfolioRepository.findAll(pageRequest);
        List<UserScoreDto> scoreboard = new ArrayList<>();
        int placeInRanking = pageNumber * pageSize + 1;
        for (Portfolio portfolio : page.getContent()) {
            scoreboard.add(new UserScoreDto(portfolio, placeInRanking));
            ++placeInRanking;
        }
        return new ScoreboardPageDto(scoreboard, pageNumber, page.getTotalPages());
    }

    private String createProfileImageUrl(String username, String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(VERSION_1 + USER_IMAGE_PATH + username + FORWARD_SLASH
                + fileName).toUriString();
    }
}
