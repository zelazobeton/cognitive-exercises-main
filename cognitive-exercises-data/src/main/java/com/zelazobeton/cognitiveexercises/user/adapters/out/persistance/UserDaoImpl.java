package com.zelazobeton.cognitiveexercises.user.adapters.out.persistance;

import java.util.Optional;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.zelazobeton.cognitiveexercises.shared.AbstractJpaDao;
import com.zelazobeton.cognitiveexercises.user.domain.User;

@Repository
class UserDaoImpl extends AbstractJpaDao<User> implements UserDao {

    public UserDaoImpl() {
        this.setClazz(User.class);
    }

    @Override
    public Optional<User> findUserByExternalId(String externalId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        CriteriaQuery<User> select = query.select(root).where(cb.equal(root.get("externalId"), externalId));
        Query q = this.entityManager.createQuery(select);
        Object user = q.getSingleResult();
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of((User) user);
    }
}
