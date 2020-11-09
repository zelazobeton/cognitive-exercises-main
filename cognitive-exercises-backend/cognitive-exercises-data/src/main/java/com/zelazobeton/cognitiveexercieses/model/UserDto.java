package com.zelazobeton.cognitiveexercieses.model;

import java.util.Date;

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
public class UserDto {
    private String username;
    private String email;
    private String password;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private boolean isActive;
    private boolean isNotLocked;
    private PortfolioDto portfolio;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = null;
        this.lastLoginDate = user.getLastLoginDate();
        this.lastLoginDateDisplay = user.getLastLoginDateDisplay();
        this.joinDate = user.getJoinDate();
        this.isActive = user.isActive();
        this.isNotLocked = user.isNotLocked();
        this.portfolio = new PortfolioDto(user.getPortfolio());
    }
}
