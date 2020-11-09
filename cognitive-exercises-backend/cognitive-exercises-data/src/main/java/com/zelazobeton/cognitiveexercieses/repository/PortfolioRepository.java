package com.zelazobeton.cognitiveexercieses.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercieses.domain.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
