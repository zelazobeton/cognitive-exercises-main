package com.zelazobeton.cognitiveexercises.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercises.model.GameDataDto;
import com.zelazobeton.cognitiveexercises.repository.GameDataRepository;
import com.zelazobeton.cognitiveexercises.service.GameDataService;

@Service
public class GameDataDataServiceImpl implements GameDataService {
    GameDataRepository gameDataRepository;

    public GameDataDataServiceImpl(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public List<GameDataDto> getGamesData() {
        return this.gameDataRepository.findAll().stream().map(GameDataDto::new).collect(Collectors.toList());
    }
}
