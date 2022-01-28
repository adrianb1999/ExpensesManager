package com.adrian99.expensesManager.services.implementation;

import com.adrian99.expensesManager.customQueries.ExpenseQueries;
import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.model.PayMethod;
import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.repositories.ExpenseRepository;
import com.adrian99.expensesManager.services.ExpenseService;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseQueries expenseQueries;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseQueries expenseQueries) {
        this.expenseRepository = expenseRepository;
        this.expenseQueries = expenseQueries;
    }

    //CurdRepo Methods
    @Override
    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        expenseRepository.findAll().forEach(expenses::add);
        return expenses;
    }

    @Override
    public Expense findById(Long aLong) {
        return expenseRepository.findById(aLong).orElse(null);
    }

    @Override
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public void deleteById(Long aLong) {
        expenseRepository.deleteById(aLong);
    }

    //QueryDSL Methods
    @Override
    public Optional<Expense> findOne(Predicate predicate) {
        return Optional.empty();
    }

    @Override
    public Iterable<Expense> findAll(Predicate predicate) {
        return expenseRepository.findAll(predicate);
    }

    @Override
    public Iterable<Expense> findAll(Predicate predicate, Sort sort) {
        return expenseRepository.findAll(predicate, sort);
    }

    //Custom repos
    @Override
    public List<Expense> findAllByFilters(Long userId,
                                          Double amount,
                                          Double amountLessThan,
                                          Double amountGreaterThan,
                                          String date,
                                          String dateAfter,
                                          String dateBefore,
                                          PayMethod payMethod,
                                          Category category,
                                          SortBy sortBy,
                                          SortTypes sortType) {
        if (sortBy == null)
            return (List<Expense>)
                    findAll(expenseQueries
                            .customPredicate(
                                    userId,
                                    amount,
                                    amountLessThan,
                                    amountGreaterThan,
                                    date,
                                    dateAfter,
                                    dateBefore,
                                    payMethod,
                                    category));
        return (List<Expense>) findAll(expenseQueries
                        .customPredicate(
                                userId,
                                amount,
                                amountLessThan,
                                amountGreaterThan,
                                date,
                                dateAfter,
                                dateBefore,
                                payMethod,
                                category),
                ExpenseQueries.sort(sortBy, sortType == null ? SortTypes.ASC : sortType));
    }
}
