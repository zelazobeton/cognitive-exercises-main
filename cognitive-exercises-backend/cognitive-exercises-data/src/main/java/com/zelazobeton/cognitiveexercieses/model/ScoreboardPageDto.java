package com.zelazobeton.cognitiveexercieses.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScoreboardPageDto {
    List<UserScoreDto> userScores;
    int pageNumber;
    int pagesTotal;

    public ScoreboardPageDto(List<UserScoreDto> userScores, int pageNumber, int pagesTotal) {
        this.userScores = userScores;
        this.pageNumber = pageNumber;
        this.pagesTotal = pagesTotal;
    }
}
