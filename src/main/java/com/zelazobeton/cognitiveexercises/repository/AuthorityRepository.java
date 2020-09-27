package com.zelazobeton.cognitiveexercises.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zelazobeton.cognitiveexercises.domain.security.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
