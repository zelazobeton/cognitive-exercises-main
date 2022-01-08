package com.zelazobeton.cognitiveexercises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercises.domain.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
