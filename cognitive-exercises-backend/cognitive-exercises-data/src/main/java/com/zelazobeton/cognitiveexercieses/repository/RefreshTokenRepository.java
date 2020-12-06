package com.zelazobeton.cognitiveexercieses.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercieses.domain.security.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    void deleteByUser_Id(Long Id);
}
