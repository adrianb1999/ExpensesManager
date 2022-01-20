package com.adrian99.expensesManager.repositories;

import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.repositories.custom.UserCustomRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long>,
                                        QuerydslPredicateExecutor<User>,
                                        UserCustomRepository {
}
