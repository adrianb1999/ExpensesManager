package com.adrian99.expensesManager.customQueries;

import com.adrian99.expensesManager.exception.ApiRequestException;
import com.adrian99.expensesManager.model.*;
import com.adrian99.expensesManager.model.QExpense;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Set;

@Component
public class ExpenseQueries {

    public static QSort sort(SortBy sortBy, SortTypes sortType) {
        if (sortBy.equals(SortBy.DATE)) {
            if (sortType.equals(SortTypes.ASC))
                return new QSort(QExpense.expense.date.asc());
            return new QSort(QExpense.expense.date.desc());
        }
        if (sortBy.equals(SortBy.AMOUNT)) {
            if (sortType.equals(SortTypes.ASC))
                return new QSort(QExpense.expense.amount.asc());
            return new QSort(QExpense.expense.amount.desc());
        }
        return new QSort(QExpense.expense.id.asc());
    }

    public BooleanBuilder customPredicate(Long userId,
                                          Double amount,
                                          Double amountLessThan,
                                          Double amountGreaterThan,
                                          String date,
                                          String dateAfter,
                                          String dateBefore,
                                          PayMethod payMethod,
                                          Set<Category> category) {
        QExpense qExpense = QExpense.expense;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (userId != null)
            booleanBuilder.and(qExpense.users.id.eq(userId));

        booleanBuilder.and(amountPredicate(amount, amountLessThan, amountGreaterThan));
        booleanBuilder.and(datePredicate(date, dateAfter, dateBefore));

        if (payMethod != null)
            booleanBuilder.and(qExpense.payMethod.eq(payMethod));

        if (category != null) {
            booleanBuilder.and(categoryPredicate(category));
        }

        return booleanBuilder;
    }

    private BooleanBuilder categoryPredicate(Set<Category> categories) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QExpense expense = QExpense.expense;
        categories.forEach(category -> booleanBuilder.or(expense.category.eq(category)));

        return booleanBuilder;
    }

    private BooleanExpression amountPredicate(Double amount, Double amountLessThan, Double amountGreaterThan) {
        QExpense qExpense = QExpense.expense;

        if (amount != null)
            return qExpense.amount.eq(amount);

        if (amountGreaterThan != null && amountLessThan != null)
            return qExpense.amount.between(amountGreaterThan, amountLessThan);

        if (amountLessThan != null)
            return qExpense.amount.lt(amountLessThan);

        if (amountGreaterThan != null)
            return qExpense.amount.gt(amountGreaterThan);

        return null;
    }

    private BooleanExpression datePredicate(String date, String dateAfter, String dateBefore) {
        QExpense qExpense = QExpense.expense;

        if (date != null && !date.isEmpty()) {
            LocalDate localDate;
            try {
                localDate = LocalDate.parse(date);
            } catch (DateTimeException e) {
                throw new ApiRequestException("Invalid data format!");
            }
            return qExpense.date.eq(localDate);
        }

        if (dateBefore != null && dateAfter != null && !dateBefore.isEmpty() && !dateAfter.isEmpty()) {

            LocalDate localDateBefore;
            LocalDate localDateAfter;
            try {
                localDateBefore = LocalDate.parse(dateBefore);
                localDateAfter = LocalDate.parse(dateAfter);
            } catch (DateTimeException e) {
                throw new ApiRequestException("Invalid data format!");
            }

            return qExpense.date.between(localDateAfter, localDateBefore);
        }

        if (dateBefore != null && !dateBefore.isEmpty()) {

            LocalDate localDateBefore;

            try {
                localDateBefore = LocalDate.parse(dateBefore);
            } catch (DateTimeException e) {
                throw new ApiRequestException("Invalid data format!");
            }

            return qExpense.date.lt(localDateBefore);
        }

        if (dateAfter != null && !dateAfter.isEmpty()) {

            LocalDate localDateAfter;

            try {
                localDateAfter = LocalDate.parse(dateAfter);
            } catch (DateTimeException e) {
                throw new ApiRequestException("Invalid data format!");
            }

            return qExpense.date.gt(localDateAfter);
        }

        return null;
    }
}
