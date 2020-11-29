package com.zelazobeton.cognitiveexercieses.service;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.AVATAR;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.DIRECTORY_CREATED;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.NOT_AN_IMAGE_FILE;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.USER_FOLDER;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.USER_IMAGE_PATH;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zelazobeton.cognitiveexercieses.domain.Portfolio;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercieses.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercieses.model.UserScoreDto;
import com.zelazobeton.cognitiveexercieses.repository.PortfolioRepository;
import com.zelazobeton.cognitiveexercieses.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(UserRepository userRepository, PortfolioRepository portfolioRepository) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Portfolio updateAvatar(String username, MultipartFile avatar)
            throws EntityNotFoundException, IOException, NotAnImageFileException {
        User currentUser = userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        Portfolio currentPortfolio = currentUser.getPortfolio();
        if (avatar != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(avatar.getContentType())) {
                throw new NotAnImageFileException(avatar.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }

            Path userAvatarFolder = Paths.get(USER_FOLDER + FORWARD_SLASH + currentUser.getUsername() + FORWARD_SLASH + AVATAR).toAbsolutePath().normalize();
            if(!Files.exists(userAvatarFolder)) {
                Files.createDirectories(userAvatarFolder);
                log.debug(DIRECTORY_CREATED + userAvatarFolder);
            }

            for(File file: userAvatarFolder.toFile().listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
            String filename = avatar.getOriginalFilename() == null ? "avatar.jpg" : avatar.getOriginalFilename();
            Files.copy(avatar.getInputStream(), userAvatarFolder.resolve(filename), REPLACE_EXISTING);
            currentPortfolio.setAvatar(createProfileImageUrl(currentUser.getUsername(), avatar.getOriginalFilename()));
        }
        return portfolioRepository.save(currentPortfolio);
    }

    @Override
    public List<UserScoreDto> getScoreboardPage(int pageNumber, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("totalScore").descending());
        List<Portfolio> portfolioList = portfolioRepository.findAll(pageRequest).getContent();
        List<UserScoreDto> scoreboard = new ArrayList<>();
        int placeInRanking = pageNumber * pageSize + 1;
        for (int idx = 0; idx < portfolioList.size(); ++idx) {
            scoreboard.add(new UserScoreDto(portfolioList.get(idx), placeInRanking));
            ++placeInRanking;
        }
        return scoreboard;
    }

    private String createProfileImageUrl(String username, String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + fileName).toUriString();
    }
}
