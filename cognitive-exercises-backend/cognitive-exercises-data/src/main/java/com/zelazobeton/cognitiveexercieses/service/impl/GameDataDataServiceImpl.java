package com.zelazobeton.cognitiveexercieses.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercieses.model.GameDataDto;
import com.zelazobeton.cognitiveexercieses.repository.GameDataRepository;
import com.zelazobeton.cognitiveexercieses.service.GameDataService;

@Service
public class GameDataDataServiceImpl implements GameDataService {
    GameDataRepository gameDataRepository;

    public GameDataDataServiceImpl(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public List<GameDataDto> getGamesData() {
        return gameDataRepository.findAll().stream().map(GameDataDto::new).collect(Collectors.toList());
    }
}
