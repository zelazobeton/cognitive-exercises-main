package com.zelazobeton.cognitiveexercises.games.domain;

import javax.persistence.Entity;

import com.zelazobeton.cognitiveexercises.shared.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameData extends BaseEntity {
    private String title;
    private String icon;
}
