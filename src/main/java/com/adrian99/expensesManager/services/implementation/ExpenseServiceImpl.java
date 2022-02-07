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
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
                                          SortTypes sortType,
                                          Integer pageSize,
                                          Integer pageNum) {
        if (sortBy == null && pageNum == null && pageSize == null)
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
        else if(pageNum == null && pageSize == null)
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
        else if(sortBy == null)
            return findAll(expenseQueries
                    .customPredicate(
                            userId,
                            amount,
                            amountLessThan,
                            amountGreaterThan,
                            date,
                            dateAfter,
                            dateBefore,
                            payMethod,
                            category), PageRequest.of(pageNum,pageSize)).getContent();
        else return findAll(expenseQueries
                        .customPredicate(
                                userId,
                                amount,
                                amountLessThan,
                                amountGreaterThan,
                                date,
                                dateAfter,
                                dateBefore,
                                payMethod,
                                category),PageRequest.of(pageNum,pageSize,ExpenseQueries.sort(sortBy, sortType == null ? SortTypes.ASC : sortType))).getContent();
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
    public <S extends Expense> Iterable<S> saveAll(Iterable<S> entities) {
        return expenseRepository.saveAll(entities);
    }

    @Override
    public Page<Expense> findAll(Predicate predicate, Pageable pageable) {
        return expenseRepository.findAll(predicate, pageable);
    }
}
