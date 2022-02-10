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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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
    public Expense findById(Long aLong) {
        return expenseRepository.findById(aLong).orElse(null);
    }

    @Override
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public <S extends Expense> Iterable<S> saveAll(Iterable<S> entities) {
        return expenseRepository.saveAll(entities);
    }

    @Override
    public void deleteById(Long aLong) {
        expenseRepository.deleteById(aLong);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        expenseRepository.deleteAllById(longs);
    }

    //Custom repos
    @Override
    public Map<String, Object> findAllByFilters(Long userId,
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
                                                Integer pageNum) {
        Long expensesSize = count(expenseQueries
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
        if(sortBy == null)
            sortBy = SortBy.ID;

        List<Expense> expenseList;
        if (pageNum == null && pageSize == null)
            expenseList = (List<Expense>) findAll(expenseQueries
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
        else expenseList = findAll(expenseQueries
                    .customPredicate(
                            userId,
                            amount,
                            amountLessThan,
                            amountGreaterThan,
                            date,
                            dateAfter,
                            dateBefore,
                            payMethod,
                            category), PageRequest.of(pageNum, pageSize, ExpenseQueries.sort(sortBy, sortType == null ? SortTypes.ASC : sortType))).getContent();

        Map<String, Object> expensesList = new HashMap<>();
        expensesList.put("expenses", expenseList);
        expensesList.put("size", expensesSize);
        return expensesList;
    }

    @Override
    public void deleteByIdAndUserId(Long userId, Long expenseId) {
        expenseRepository.deleteByIdAndUserId(userId, expenseId);
    }

    @Override
    public List<Map<String, Object>> totalExpensesByDay(Long userId, LocalDate firstDate, LocalDate secondDate) {
        return expenseRepository.totalExpensesByDay(userId, firstDate, secondDate);
    }

    @Override
    public List<Map<String, Double>> totalExpensesByLastNMonths(Long userId, Integer numOfMonth) {
        return expenseRepository.totalExpensesByLastNMonths(userId, numOfMonth);
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

    @Override
    public Page<Expense> findAll(Predicate predicate, Pageable pageable) {
        return expenseRepository.findAll(predicate, pageable);
    }

    @Override
    public long count(Predicate predicate) {
        return expenseRepository.count(predicate);
    }
}
