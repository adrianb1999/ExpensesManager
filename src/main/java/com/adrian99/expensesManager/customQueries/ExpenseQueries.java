package com.adrian99.expensesManager.customQueries;

import com.adrian99.expensesManager.model.*;
import com.adrian99.expensesManager.model.QCategory;
import com.adrian99.expensesManager.model.QExpense;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExpenseQueries {


    EntityManager em;

    public ExpenseQueries(EntityManager em) {
        this.em = em;
    }

    public static QSort sort(SortBy sortBy, SortTypes sortType) {
        if (sortBy.equals(SortBy.DATE)) {
            if (sortType.equals(SortTypes.ASC))
                return new QSort(QExpense.expense.date.asc());
            return new QSort(QExpense.expense.date.desc());
        }
        if (sortType.equals(SortTypes.ASC))
            return new QSort(QExpense.expense.amount.asc());
        return new QSort(QExpense.expense.amount.desc());
    }

    public BooleanBuilder customPredicate(Long userId,
                                          Double amount,
                                          Double amountLessThan,
                                          Double amountGreaterThan,
                                          String date,
                                          String dateAfter,
                                          String dateBefore,
                                          PayMethod payMethod,
                                          List<String> categories) {
        QExpense qExpense = QExpense.expense;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (userId != null)
            booleanBuilder.and(qExpense.users.id.eq(userId));

        booleanBuilder.and(amountPredicate(amount, amountLessThan, amountGreaterThan));
        booleanBuilder.and(datePredicate(date, dateAfter, dateBefore));

        if (categories != null)
            booleanBuilder.and(categoriesPredicate(categories));

        if (payMethod != null)
            booleanBuilder.and(qExpense.payMethod.eq(payMethod));

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

        if (date != null) {
            LocalDate localDate = LocalDate.parse(date);
            return qExpense.date.eq(localDate);
        }

        if (dateBefore != null && dateAfter != null) {
            LocalDate localDateBefore = LocalDate.parse(dateBefore);
            LocalDate localDateAfter = LocalDate.parse(dateAfter);
            return qExpense.date.between(localDateAfter, localDateBefore);
        }

        if (dateBefore != null) {
            LocalDate localDateBefore = LocalDate.parse(dateBefore);
            return qExpense.date.lt(localDateBefore);
        }

        if (dateAfter != null) {
            LocalDate localDateAfter = LocalDate.parse(dateAfter);
            return qExpense.date.gt(localDateAfter);
        }

        return null;
    }


    private BooleanBuilder categoriesPredicate(List<String> categories) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QExpense qExpense = QExpense.expense;
        List<Category> categoryList = new ArrayList<>();

        for (String name : categories) {
            JPAQuery<Category> query = new JPAQuery<>(em);
            QCategory qCategory = QCategory.category;

            Category category = query.select(qCategory)
                    .from(qCategory)
                    .where(qCategory.name.eq(name))
                    .fetchOne();

            categoryList.add(category);
        }

        categoryList.forEach(category ->
                booleanBuilder.or(qExpense.categories.contains(category)));

        return booleanBuilder;
    }
}
