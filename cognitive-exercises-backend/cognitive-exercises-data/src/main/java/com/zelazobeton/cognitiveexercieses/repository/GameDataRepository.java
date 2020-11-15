package com.zelazobeton.cognitiveexercieses.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercieses.domain.GameData;

public interface GameDataRepository extends JpaRepository<GameData, Long> {
}
