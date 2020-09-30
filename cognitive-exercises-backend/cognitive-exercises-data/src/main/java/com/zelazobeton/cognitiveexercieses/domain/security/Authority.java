package com.zelazobeton.cognitiveexercieses.domain.security;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.zelazobeton.cognitiveexercieses.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Authority extends BaseEntity {
    private String permission;

    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}
