package com.adrian99.expensesManager.services;

import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.repositories.custom.UserCustomRepository;

public interface UserService extends CrudService<User,Long>,
                                     UserCustomRepository,
                                     QueryDslService<User>{
    String generateToken(User newUser);
}
