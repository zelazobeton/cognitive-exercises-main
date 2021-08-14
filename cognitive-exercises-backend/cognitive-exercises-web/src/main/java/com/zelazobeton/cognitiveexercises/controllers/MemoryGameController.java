package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.MEMORY_IMG_FOLDER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.model.memory.MemoryBoardDto;
import com.zelazobeton.cognitiveexercieses.service.MemoryGameService;
import com.zelazobeton.cognitiveexercieses.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercieses.service.ResourceService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.HttpResponse;
import com.zelazobeton.cognitiveexercises.constant.MessageConstants;

@RestController
@RequestMapping(path = "/memory")
public class MemoryGameController extends ExceptionHandling {
    private final MemoryGameService memoryGameService;
    private final ResourceService resourceService;

    public MemoryGameController(ExceptionMessageService exceptionMessageService, MemoryGameService memoryGameService, ResourceService resourceService) {
        super(exceptionMessageService);
        this.memoryGameService = memoryGameService;
        this.resourceService = resourceService;
    }

    @GetMapping(path = "/continue", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<MemoryBoardDto> getMemoryBoard(@AuthenticationPrincipal User user) {
        MemoryBoardDto board = memoryGameService.getSavedMemoryBoardDto(user.getPortfolio().getId());
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PostMapping(path = "/new-game", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<MemoryBoardDto> getNewMemoryBoard(@AuthenticationPrincipal User user,
            @RequestBody Integer difficultyLvl) {
        MemoryBoardDto board = memoryGameService.getNewMemoryBoardDto(user.getPortfolio().getId(), difficultyLvl);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping(path = "/img/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getMemoryTileImage(@PathVariable("fileName") String fileName) throws IOException {
        return resourceService.getResource(MEMORY_IMG_FOLDER + FORWARD_SLASH + fileName);
    }

    @PostMapping(path = "/save-game")
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<HttpResponse> saveGame(@AuthenticationPrincipal User user,
            @RequestBody MemoryBoardDto memoryBoardDto) {
        memoryGameService.saveGame(user.getPortfolio().getId(), memoryBoardDto);
        return new ResponseEntity<>(
                new HttpResponse(OK, exceptionMessageService.getMessage(MessageConstants.MEMORY_GAME_CONTROLLER_GAME_SAVED)), OK);
    }

    @PostMapping(path = "/save-score")
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<Integer> saveScore(@AuthenticationPrincipal User user,
            @RequestBody MemoryBoardDto memoryBoardDto) {
        Integer score = memoryGameService.saveScore(user.getPortfolio().getId(), memoryBoardDto);
        return new ResponseEntity<>(score, HttpStatus.OK);
    }
}
