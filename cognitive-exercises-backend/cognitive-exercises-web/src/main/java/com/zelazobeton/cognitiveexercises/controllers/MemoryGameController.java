package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.MEMORY_IMG_FOLDER;
import static org.springframework.http.HttpStatus.OK;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercieses.model.memory.MemoryBoardDto;
import com.zelazobeton.cognitiveexercieses.service.MemoryGameService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.HttpResponse;

@RestController
@RequestMapping(path = "/memory")
public class MemoryGameController extends ExceptionHandling {
    public static final String GAME_SAVED = "Game saved";

    private MemoryGameService memoryGameService;

    public MemoryGameController(MemoryGameService memoryGameService) {
        this.memoryGameService = memoryGameService;
    }

    @GetMapping(path = "/continue", produces = { "application/json" })
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<MemoryBoardDto> getMemoryBoard(@AuthenticationPrincipal User user)
            throws EntityNotFoundException {
        MemoryBoardDto board = memoryGameService.getSavedMemoryBoardDto(user.getPortfolio().getId());
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping(path = "/new-game", produces = { "application/json" })
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

    @PostMapping(path = "/save")
    @PreAuthorize("hasAuthority('user.update')")
    public ResponseEntity<HttpResponse> saveGame(@AuthenticationPrincipal User user,
            @RequestBody MemoryBoardDto memoryBoardDto) throws EntityNotFoundException{
        memoryGameService.saveGame(user.getPortfolio().getId(), memoryBoardDto);
        return new ResponseEntity<>(new HttpResponse(OK, GAME_SAVED), OK);
    }
}
