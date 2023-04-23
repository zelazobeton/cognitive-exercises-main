package com.zelazobeton.cognitiveexercises.user.adapters.out.authorization;

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
public class AdminAccessTokenDto {
    private String access_token;
    private int expires_in;
    private int refresh_expires_in;
    private String token_type;
    private String scope;
}
