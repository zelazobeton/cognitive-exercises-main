package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.AVATAR;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.USER_FOLDER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zelazobeton.cognitiveexercieses.domain.Portfolio;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercieses.model.PortfolioDto;
import com.zelazobeton.cognitiveexercieses.model.UserScoreDto;
import com.zelazobeton.cognitiveexercieses.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercieses.service.PortfolioService;
import com.zelazobeton.cognitiveexercieses.service.ResourceService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;

@RestController
@RequestMapping(path = "/portfolio")
public class PortfolioController extends ExceptionHandling {
    private final PortfolioService portfolioService;
    private final ResourceService resourceService;

    public PortfolioController(ExceptionMessageService exceptionMessageService,
            PortfolioService portfolioService,
            ResourceService resourceService) {
        super(exceptionMessageService);
        this.portfolioService = portfolioService;
        this.resourceService = resourceService;
    }

    @PostMapping(path = "/update-avatar", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<PortfolioDto> updatePortfolio(@AuthenticationPrincipal User user,
            @RequestParam("avatar") MultipartFile avatar) throws IOException, NotAnImageFileException {
        Portfolio updatedPortfolio = portfolioService.updateAvatar(user.getUsername(), avatar);
        return new ResponseEntity<>(new PortfolioDto(updatedPortfolio), HttpStatus.OK);
    }

    @GetMapping(path = "/avatar/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
            throws IOException {
        return resourceService.getResource(USER_FOLDER + FORWARD_SLASH + username + AVATAR + FORWARD_SLASH + fileName);
    }

    @GetMapping(path = "/scoreboard", produces = { "application/json" })
    public ResponseEntity<List<UserScoreDto>> getScoreboard(@AuthenticationPrincipal User user,
            @RequestParam("page") String pageNum, @RequestParam("size") String pageSize) throws IOException {
        List<UserScoreDto> scoreboard = portfolioService.getScoreboardPage(
                Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        return new ResponseEntity<>(scoreboard, HttpStatus.OK);
    }
}
