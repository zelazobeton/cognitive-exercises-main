package com.zelazobeton.cognitiveexercises.user.application;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.zelazobeton.cognitiveexercises.user.domain.User;

public interface UserRepository {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByExternalId(String externalId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String username);

    User findAllUsers();

    List<User> findAll();

    List<User> findAll(Sort sort);

    List<User> findAllById(Iterable<Long> ids);

    User save(User entity);

    void delete(User entity);

    void saveAll(Iterable<User> users);
}
