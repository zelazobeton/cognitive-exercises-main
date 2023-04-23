package com.zelazobeton.cognitiveexercises.games.adapters.out;

import java.util.List;
import java.util.Optional;

import com.zelazobeton.cognitiveexercises.games.application.GameDataRepository;
import com.zelazobeton.cognitiveexercises.games.domain.GameData;
import com.zelazobeton.cognitiveexercises.shared.PersistenceAdapter;

@PersistenceAdapter
class GameDataPersistanceAdapter implements GameDataRepository {

    private final GameDataJpaRepository gameDataJpaRepository;

    public GameDataPersistanceAdapter(GameDataJpaRepository gameDataJpaRepository) {
        this.gameDataJpaRepository = gameDataJpaRepository;
    }

    @Override
    public GameData save(GameData entity) {
        return this.gameDataJpaRepository.save(entity);
    }

    @Override
    public Optional<GameData> findById(Long id) {
        return this.gameDataJpaRepository.findById(id);
    }

    @Override
    public void delete(GameData entity) {
        this.gameDataJpaRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        this.gameDataJpaRepository.deleteById(id);
    }

    @Override
    public List<GameData> findAll() {
        return this.gameDataJpaRepository.findAll();
    }
}
