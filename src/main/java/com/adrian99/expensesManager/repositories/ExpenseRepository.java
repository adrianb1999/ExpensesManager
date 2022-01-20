package com.adrian99.expensesManager.repositories;

import com.adrian99.expensesManager.model.Expense;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseRepository extends CrudRepository<Expense,Long>, QuerydslPredicateExecutor<Expense> {
}
