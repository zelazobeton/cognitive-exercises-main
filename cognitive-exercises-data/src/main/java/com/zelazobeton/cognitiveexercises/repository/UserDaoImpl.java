package com.zelazobeton.cognitiveexercises.repository;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.exception.UserNotFoundException;

@Repository
public class UserDaoImpl extends AbstractJpaDao<User> implements UserDao {

    public UserDaoImpl() {
        this.setClazz(User.class);
    }

    @Override
    public User findUserByExternalId(String externalId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        CriteriaQuery<User> select = query.select(root).where(cb.equal(root.get("externalId"), externalId));
        Query q = this.entityManager.createQuery(select);
        Object user = q.getSingleResult();
        if (user == null) {
            throw new UserNotFoundException("User with externalId: " + externalId + " does not exist");
        }
        return (User) user;
    }
}
