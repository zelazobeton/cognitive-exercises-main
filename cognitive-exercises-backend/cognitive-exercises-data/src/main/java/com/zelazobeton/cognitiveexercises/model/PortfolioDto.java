package com.zelazobeton.cognitiveexercises.model;

import com.zelazobeton.cognitiveexercises.domain.Portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {
    private String avatar;
    private Long totalScore;

    public PortfolioDto(Portfolio portfolio) {
        this.avatar = portfolio.getAvatar();
        this.totalScore = portfolio.getTotalScore();
    }
}
