package com.adrian99.expensesManager.repositories.custom.implementation;

import com.adrian99.expensesManager.customQueries.UserQueries;
import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.repositories.custom.UserCustomRepository;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    UserQueries userQueries;

    public UserCustomRepositoryImpl(UserQueries userQueries) {
        this.userQueries = userQueries;
    }

    @Override
    public User findByUsername(String username) {
        return userQueries.findByUsername(username);
    }
}
