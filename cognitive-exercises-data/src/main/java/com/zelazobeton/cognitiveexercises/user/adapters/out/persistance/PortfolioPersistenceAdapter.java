package com.zelazobeton.cognitiveexercises.user.adapters.out.persistance;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zelazobeton.cognitiveexercises.shared.PersistenceAdapter;
import com.zelazobeton.cognitiveexercises.user.application.PortfolioRepository;
import com.zelazobeton.cognitiveexercises.user.domain.Portfolio;

@PersistenceAdapter
class PortfolioPersistenceAdapter implements PortfolioRepository {

    private final PortfolioJpaRepository portfolioJpaRepository;

    public PortfolioPersistenceAdapter(PortfolioJpaRepository portfolioJpaRepository) {
        this.portfolioJpaRepository = portfolioJpaRepository;
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return this.portfolioJpaRepository.save(portfolio);
    }

    @Override
    public List<Portfolio> findAll() {
        return this.portfolioJpaRepository.findAll();
    }

    @Override
    public Page<Portfolio> findAll(Pageable pageable) {
        return this.portfolioJpaRepository.findAll(pageable);
    }
}
