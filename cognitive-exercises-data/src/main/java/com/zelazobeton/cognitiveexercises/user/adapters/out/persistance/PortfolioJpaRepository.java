package com.zelazobeton.cognitiveexercises.user.adapters.out.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercises.user.domain.Portfolio;

interface PortfolioJpaRepository extends JpaRepository<Portfolio, Long> {
}
