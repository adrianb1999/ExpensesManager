package com.adrian99.expensesManager.customQueries;

import com.adrian99.expensesManager.model.QUser;
import com.adrian99.expensesManager.model.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class UserQueries {

    private final EntityManager entityManager;

    public UserQueries(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User findByUsername(String username) {
        JPAQuery<User> query = new JPAQuery<>(entityManager);
        QUser qUser = QUser.user;
        return query.select(qUser).from(qUser)
                .where(qUser.username.eq(username)).fetchFirst();
    }

    public BooleanExpression username(String username) {
        return QUser.user.username.eq(username);
    }
}
