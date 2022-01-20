package com.adrian99.expensesManager.services;

import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.repositories.custom.UserCustomRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserService extends CrudService<User,Long>,
                                     UserCustomRepository,
                                     QuerydslPredicateExecutor<User> {
}
