package com.adrian99.expensesManager.repositories;

import com.adrian99.expensesManager.emailVerification.VerificationToken;
import com.adrian99.expensesManager.repositories.custom.VerificationTokenCustomRepository;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends
        CrudRepository<VerificationToken, Long>, VerificationTokenCustomRepository {
}
