package com.zelazobeton.cognitiveexercieses.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zelazobeton.cognitiveexercieses.domain.security.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String username);
}
