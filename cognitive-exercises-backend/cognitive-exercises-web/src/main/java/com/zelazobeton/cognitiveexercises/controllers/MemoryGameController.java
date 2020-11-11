package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.MEMORY_IMG_FOLDER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercieses.model.memory.MemoryBoardDto;
import com.zelazobeton.cognitiveexercieses.service.MemoryGameService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;

@RestController
@RequestMapping(path = "/memory")
public class MemoryGameController extends ExceptionHandling {
    private MemoryGameService memoryGameService;

    public MemoryGameController(MemoryGameService memoryGameService) {
        this.memoryGameService = memoryGameService;
    }

    @GetMapping(path = "/continue")
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<MemoryBoardDto> getMemoryBoard(@AuthenticationPrincipal User user)
            throws EntityNotFoundException {
        MemoryBoardDto board = memoryGameService.getSavedMemoryBoardDto(user.getPortfolio().getId());
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping(path = "/new-game")
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<MemoryBoardDto> getNewMemoryBoard(@AuthenticationPrincipal User user)
            throws EntityNotFoundException {
        MemoryBoardDto board = memoryGameService.getNewMemoryBoardDto(user.getPortfolio().getId());
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping(path = "/img/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getMemoryTileImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(MEMORY_IMG_FOLDER + FORWARD_SLASH + fileName));
    }

}
