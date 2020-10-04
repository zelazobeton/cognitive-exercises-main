package com.zelazobeton.cognitiveexercieses.model;

import com.zelazobeton.cognitiveexercieses.domain.security.User;

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
public class UserScoringDto {
    private String username;

    public UserScoringDto(User user) {
        this.username = user.getUsername();
    }
}
