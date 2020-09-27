package com.zelazobeton.cognitiveexercises.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercises.domain.security.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String role);
}
