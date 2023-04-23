package com.zelazobeton.cognitiveexercises.games.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercises.games.domain.GameData;

public interface GameDataJpaRepository extends JpaRepository<GameData, Long> {
}
