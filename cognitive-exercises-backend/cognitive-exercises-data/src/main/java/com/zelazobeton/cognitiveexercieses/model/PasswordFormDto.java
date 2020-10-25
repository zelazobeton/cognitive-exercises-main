package com.zelazobeton.cognitiveexercieses.model;

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
public class PasswordFormDto {
    private String oldPassword;
    private String newPassword;
}
