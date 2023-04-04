package com.zelazobeton.cognitiveexercises.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zelazobeton.cognitiveexercises.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByExternalId(String externalId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String username);

    // JPQL
    @Query("select s from User s where s.externalId = ?1")
    User findUserByExternalIdWithJpql(String externalId);

    // native SQL
    @Query(value = "SELECT * FROM USERS", nativeQuery = true)
    User findAllUsersNativeSql();
}
