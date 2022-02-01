package com.zelazobeton.cognitiveexercises.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class Portfolio extends BaseEntity {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;
    private String avatar;
    private Long totalScore;

    public Portfolio(User user, String avatar, Long totalScore) {
        this.user = user;
        this.avatar = avatar;
        this.totalScore = totalScore;
        user.setPortfolio(this);
    }

    public Long incrementTotalScore(Integer score) {
        this.totalScore += score;
        return this.totalScore;
    }
}
