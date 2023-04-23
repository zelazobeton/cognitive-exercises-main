package com.zelazobeton.cognitiveexercises.user.adapters.out.persistance;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.zelazobeton.cognitiveexercises.shared.PersistenceAdapter;
import com.zelazobeton.cognitiveexercises.user.application.UserRepository;
import com.zelazobeton.cognitiveexercises.user.domain.User;

@PersistenceAdapter
class UserPersistenceAdapter implements UserRepository {

    UserJpaRepository userJpaRepository;
    UserDao userDao;


    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserDao userDao) {
        this.userJpaRepository = userJpaRepository;
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return this.userJpaRepository.findUserByUsername(username);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return this.userJpaRepository.findUserByEmail(email);
    }

    @Override
    public Optional<User> findUserByExternalId(String externalId) {
        return this.userDao.findUserByExternalId(externalId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String username) {
        return this.userJpaRepository.existsByEmail(username);
    }

    @Override
    public User findAllUsers() {
        return this.userJpaRepository.findAllUsers();
    }

    @Override
    public List<User> findAll() {
        return this.userJpaRepository.findAll();
    }

    @Override
    public List<User> findAll(Sort sort) {
        return this.userJpaRepository.findAll(sort);
    }

    @Override
    public List<User> findAllById(Iterable<Long> ids) {
        return this.userJpaRepository.findAllById(ids);
    }

    @Override
    public User save(User entity) {
        return this.userJpaRepository.save(entity);
    }

    @Override
    public void delete(User entity) {
        this.userJpaRepository.delete(entity);
    }

    @Override
    public void saveAll(Iterable<User> users) {
        this.userJpaRepository.saveAll(users);
    }
}
