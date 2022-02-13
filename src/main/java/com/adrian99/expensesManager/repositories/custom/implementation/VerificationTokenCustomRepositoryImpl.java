package com.adrian99.expensesManager.repositories.custom.implementation;

import com.adrian99.expensesManager.emailVerification.QVerificationToken;
import com.adrian99.expensesManager.emailVerification.VerificationToken;
import com.adrian99.expensesManager.repositories.custom.VerificationTokenCustomRepository;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class VerificationTokenCustomRepositoryImpl implements VerificationTokenCustomRepository {

    private final EntityManager entityManager;

    public VerificationTokenCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public VerificationToken findByToken(String token) {

        QVerificationToken verificationToken = QVerificationToken.verificationToken;
        JPAQuery<VerificationToken> query = new JPAQuery<>(entityManager);

        return query.select(verificationToken)
                .from(verificationToken)
                .where(verificationToken.token.eq(token))
                .fetchFirst();
    }
}
