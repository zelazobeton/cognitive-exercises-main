package com.zelazobeton.cognitiveexercises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercises.domain.memory.MemoryBoard;

public interface MemoryBoardRepository extends JpaRepository<MemoryBoard, Long> {

}
