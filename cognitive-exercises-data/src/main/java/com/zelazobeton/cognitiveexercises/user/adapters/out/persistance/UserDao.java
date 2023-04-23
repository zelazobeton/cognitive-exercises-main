package com.zelazobeton.cognitiveexercises.user.adapters.out.persistance;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.zelazobeton.cognitiveexercises.user.domain.User;

@Repository
interface UserDao {

    Optional<User> findUserByExternalId(String externalId);
}
