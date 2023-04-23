package com.zelazobeton.cognitiveexercises.user.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zelazobeton.cognitiveexercises.user.domain.Portfolio;

public interface PortfolioRepository {
    Portfolio save(Portfolio portfolio);

    List<Portfolio> findAll();

    Page<Portfolio> findAll(Pageable pageable);
}
