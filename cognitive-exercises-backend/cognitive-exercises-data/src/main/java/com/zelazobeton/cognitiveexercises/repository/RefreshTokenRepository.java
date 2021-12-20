package com.zelazobeton.cognitiveexercises.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercises.domain.security.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    void deleteByUser_Id(Long Id);
}
