package com.zelazobeton.cognitiveexercises.games.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercises.games.adapters.out.GameDataDto;

@Service
class GameDataServiceImpl implements GameDataService {

    private GameDataRepository gameDataRepository;

    public GameDataServiceImpl(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public List<GameDataDto> getGamesData() {
        return this.gameDataRepository.findAll().stream().map(GameDataDto::new).collect(Collectors.toList());
    }
}
