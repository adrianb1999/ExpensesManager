package com.adrian99.expensesManager.services;

import com.adrian99.expensesManager.emailVerification.VerificationToken;
import com.adrian99.expensesManager.repositories.VerificationTokenRepository;
import com.adrian99.expensesManager.repositories.custom.VerificationTokenCustomRepository;

public interface VerificationTokenService extends CrudService<VerificationToken, Long>,
                                                  VerificationTokenCustomRepository {
    VerificationToken isTokenValid(String token);
}
