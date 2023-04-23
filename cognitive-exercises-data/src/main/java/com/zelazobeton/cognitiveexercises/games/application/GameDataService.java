package com.zelazobeton.cognitiveexercises.games.application;

import java.util.List;

import com.zelazobeton.cognitiveexercises.games.adapters.out.GameDataDto;

public interface GameDataService {
    List<GameDataDto> getGamesData();
}
