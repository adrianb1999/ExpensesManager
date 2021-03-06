package com.adrian99.expensesManager.repositories.custom;

import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.model.PayMethod;
import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ExpenseCustomRepository {
    Map<String, Object> findAllByFilters(Long userId,
                                   Double amount,
                                   Double amountLessThan,
                                   Double amountGreaterThan,
                                   String date,
                                   String dateAfter,
                                   String dateBefore,
                                   PayMethod payMethod,
                                   Set<Category> category,
                                   SortBy sortBy,
                                   SortTypes sortType,
                                   Integer pageSize,
                                   Integer pageNum);
    void deleteByIdAndUserId(Long userId, Long expenseId);
    List<Map<String, Object>> totalExpensesByDay(Long userId, LocalDate firstDate, LocalDate secondDate);
    List<Map<String, Object>> totalExpensesByLastNMonthsByCategory(Long userId, Integer numOfMonth);
    Map<String, Double> dayAverage(Long userId, LocalDate firstDate, LocalDate secondDate);
    Map<String, Double> monthAverage(Long userId, LocalDate firstDate, LocalDate secondDate);
    Map<String, Object> totalSpent (Long userId);
}
