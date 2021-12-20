package com.zelazobeton.cognitiveexercises.domain;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameData extends BaseEntity {
    private String title;
    private String icon;
}
