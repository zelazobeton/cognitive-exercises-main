package com.zelazobeton.cognitiveexercises.games.adapters.out;

import com.zelazobeton.cognitiveexercises.games.domain.GameData;

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
public class GameDataDto {
    private String title;
    private String icon;

    public GameDataDto(GameData gameData) {
        this.title = gameData.getTitle();
        this.icon = gameData.getIcon();
    }
}
