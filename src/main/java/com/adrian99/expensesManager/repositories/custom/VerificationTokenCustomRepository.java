package com.adrian99.expensesManager.repositories.custom;

import com.adrian99.expensesManager.emailVerification.VerificationToken;

public interface VerificationTokenCustomRepository {
    VerificationToken findByToken(String token);
}
