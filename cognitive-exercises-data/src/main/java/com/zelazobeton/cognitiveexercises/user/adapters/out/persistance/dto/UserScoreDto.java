package com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto;

import com.zelazobeton.cognitiveexercises.user.domain.Portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserScoreDto {
    private Integer place;
    private String username;
    private PortfolioDto portfolio;

    public UserScoreDto(Portfolio portfolio, int place) {
        this.place = place;
        this.username = portfolio.getUser().getUsername();
        this.portfolio = new PortfolioDto(portfolio);
    }
}
