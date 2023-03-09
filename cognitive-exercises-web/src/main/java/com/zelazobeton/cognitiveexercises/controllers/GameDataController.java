package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercises.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.GAMES_DATA_FOLDER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercises.model.GameDataDto;
import com.zelazobeton.cognitiveexercises.service.GameDataService;
import com.zelazobeton.cognitiveexercises.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercises.service.ResourceService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;

import io.micrometer.core.instrument.Counter;

@RestController
@RequestMapping(path = "/games/v1")
public class GameDataController extends ExceptionHandling {
    private final GameDataService gamesDataService;
    private final ResourceService resourceService;
    private final Counter getGamesCounter;

    public GameDataController(ExceptionMessageService exceptionMessageService, GameDataService gamesDataService,
            ResourceService resourceService, Counter getGamesCounter) {
        super(exceptionMessageService);
        this.gamesDataService = gamesDataService;
        this.resourceService = resourceService;
        this.getGamesCounter = getGamesCounter;
    }

    @GetMapping(path = "/data", produces = { "application/json" })
    public ResponseEntity<List<GameDataDto>> getGames() {
        this.getGamesCounter.increment();
        return new ResponseEntity<>(this.gamesDataService.getGamesData(), HttpStatus.OK);
    }

    @GetMapping(path = "/icon/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getMemoryTileImage(@PathVariable("fileName") String fileName) throws IOException {
        return this.resourceService.getResource(GAMES_DATA_FOLDER + FORWARD_SLASH + fileName);
    }
}
