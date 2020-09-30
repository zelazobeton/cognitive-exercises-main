package com.zelazobeton.cognitiveexercieses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zelazobeton.cognitiveexercieses.domain.security.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
