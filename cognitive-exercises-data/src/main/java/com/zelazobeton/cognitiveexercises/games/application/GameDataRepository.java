package com.zelazobeton.cognitiveexercises.games.application;

import java.util.List;
import java.util.Optional;

import com.zelazobeton.cognitiveexercises.games.domain.GameData;

public interface GameDataRepository {
    GameData save(GameData entity);

    Optional<GameData> findById(Long id);

    void delete(GameData entity);

    void deleteById(Long id);

    List<GameData> findAll();
}
