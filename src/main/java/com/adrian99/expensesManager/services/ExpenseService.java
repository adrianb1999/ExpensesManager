package com.adrian99.expensesManager.services;

import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.repositories.custom.ExpenseCustomRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ExpenseService extends CrudService<Expense,Long>,
                                        QuerydslPredicateExecutor<Expense>,
                                        ExpenseCustomRepository {
}
