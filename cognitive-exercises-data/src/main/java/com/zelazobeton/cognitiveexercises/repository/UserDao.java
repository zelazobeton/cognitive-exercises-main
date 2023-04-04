package com.zelazobeton.cognitiveexercises.repository;

import org.springframework.stereotype.Repository;

import com.zelazobeton.cognitiveexercises.domain.User;

@Repository
public interface UserDao {

    User findUserByExternalId(String externalId);
}
