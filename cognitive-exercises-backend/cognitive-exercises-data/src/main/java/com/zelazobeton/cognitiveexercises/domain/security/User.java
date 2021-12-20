package com.zelazobeton.cognitiveexercises.domain.security;

import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.zelazobeton.cognitiveexercises.domain.BaseEntity;
import com.zelazobeton.cognitiveexercises.domain.Portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USERS") //'USER' is reserved word in Oracle
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    @Builder.Default private Date lastLoginDate = null;
    @Builder.Default private Date lastLoginDateDisplay = null;
    @Builder.Default private Date joinDate = new Date();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Portfolio portfolio;

    @Singular
    @ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "ID") },
            inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") })
    private Set<Role> roles;
    @Builder.Default private boolean isActive = true;
    @Builder.Default private boolean isNotLocked = true;
}
