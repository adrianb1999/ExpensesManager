package com.adrian99.expensesManager.repositories.custom;

import com.adrian99.expensesManager.model.User;

public interface UserCustomRepository {
    User findByUsername(String username);
}
