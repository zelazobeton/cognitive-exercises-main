package com.zelazobeton.cognitiveexercises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercises.domain.GameData;

public interface GameDataRepository extends JpaRepository<GameData, Long> {
}
