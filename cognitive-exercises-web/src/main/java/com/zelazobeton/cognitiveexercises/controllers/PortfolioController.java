package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercises.shared.FileConstants.AVATAR;
import static com.zelazobeton.cognitiveexercises.shared.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercises.shared.FileConstants.USER_FOLDER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.PortfolioDto;
import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.ScoreboardPageDto;
import com.zelazobeton.cognitiveexercises.exception.ExceptionMessageService;
import com.zelazobeton.cognitiveexercises.user.application.PortfolioService;
import com.zelazobeton.cognitiveexercises.resource.application.ResourceService;

//@CrossOrigin("*")
@RestController
@RequestMapping(path = "/portfolio/v1")
public class PortfolioController extends ExceptionHandling {
    private final PortfolioService portfolioService;
    private final ResourceService resourceService;

    public PortfolioController(ExceptionMessageService exceptionMessageService, PortfolioService portfolioService,
            ResourceService resourceService) {
        super(exceptionMessageService);
        this.portfolioService = portfolioService;
        this.resourceService = resourceService;
    }

    @PostMapping(path = "/avatar", headers=("content-type=multipart/*"))
    @PreAuthorize("hasAuthority(app-user)")
    public ResponseEntity<PortfolioDto> updateAvatar(Principal principal,
            @RequestParam("avatar") MultipartFile avatar) throws IOException, NotAnImageFileException {
        return new ResponseEntity<>(this.portfolioService.updateAvatar(principal.getName(), avatar), HttpStatus.OK);
    }

    @GetMapping(path = "/avatar/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
            throws IOException {
        return this.resourceService.getResource(
                USER_FOLDER + FORWARD_SLASH + username + AVATAR + FORWARD_SLASH + fileName);
    }

    @GetMapping(path = "/scoreboard", produces = { "application/json" })
    public ResponseEntity<ScoreboardPageDto> getScoreboard(@RequestParam("page") String pageNum,
            @RequestParam("size") String pageSize) {
        return new ResponseEntity<>(
                this.portfolioService.getScoreboardPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize)),
                HttpStatus.OK);
    }
}
