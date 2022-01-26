package com.adrian99.expensesManager.services;

import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.repositories.custom.ExpenseCustomRepository;

public interface ExpenseService extends CrudService<Expense,Long>,
                                        QueryDslService<Expense>,
                                        ExpenseCustomRepository {
}
