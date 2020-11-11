package com.zelazobeton.cognitiveexercieses.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryImg;

public interface MemoryImgRepository extends JpaRepository<MemoryImg, Long> {
    Optional<MemoryImg> findByAddress(String address);
}
