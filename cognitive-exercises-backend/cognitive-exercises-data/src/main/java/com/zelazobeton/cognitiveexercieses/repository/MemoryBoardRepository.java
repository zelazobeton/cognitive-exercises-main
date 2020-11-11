package com.zelazobeton.cognitiveexercieses.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryBoard;

public interface MemoryBoardRepository extends JpaRepository<MemoryBoard, Long> {

}
