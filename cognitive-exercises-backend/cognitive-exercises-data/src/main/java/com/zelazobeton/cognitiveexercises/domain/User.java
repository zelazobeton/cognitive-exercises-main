package com.zelazobeton.cognitiveexercises.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "USERS") //'USER' is reserved word in Oracle
public class User extends BaseEntity {
    @Builder.Default private String externalId = null;
    private String username;
    private String email;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Portfolio portfolio;
}
