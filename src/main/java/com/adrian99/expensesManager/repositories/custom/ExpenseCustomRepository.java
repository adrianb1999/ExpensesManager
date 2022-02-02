package com.adrian99.expensesManager.repositories.custom;

import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.model.PayMethod;
import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseCustomRepository {
    List<Expense> findAllByFilters(Long userId,
                                   Double amount,
                                   Double amountLessThan,
                                   Double amountGreaterThan,
                                   String date,
                                   String dateAfter,
                                   String dateBefore,
                                   PayMethod payMethod,
                                   Category category,
                                   SortBy sortBy,
                                   SortTypes sortType);

    void deleteByIdAndUserId(Long userId, Long expenseId);
    List<Map<String, Object>> totalExpensesByDay(Long userId, LocalDate firstDate, LocalDate secondDate);
}
