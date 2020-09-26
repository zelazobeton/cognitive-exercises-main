package com.zelazobeton.cognitiveexercises.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zelazobeton.cognitiveexercises.domain.security.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
}
