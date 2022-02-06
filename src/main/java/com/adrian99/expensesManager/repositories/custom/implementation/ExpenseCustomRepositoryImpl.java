package com.adrian99.expensesManager.repositories.custom.implementation;

import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.model.PayMethod;
import com.adrian99.expensesManager.model.QExpense;
import com.adrian99.expensesManager.repositories.custom.ExpenseCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExpenseCustomRepositoryImpl implements ExpenseCustomRepository {

    private final EntityManager em;

    public ExpenseCustomRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override  //TODO Find a better way
    public List<Expense> findAllByFilters(Long userId, Double amount, Double amountLessThan, Double amountGreaterThan, String date, String dateAfter, String dateBefore, PayMethod payMethod, Category category, SortBy sortBy, SortTypes sortType) {
        return null;
    }

    @Transactional
    @Override
    public void deleteByIdAndUserId(Long userId, Long expenseId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        queryFactory.delete(expense)
                .where(
                        expense.id.eq(expenseId).and(
                                expense.users.id.eq(userId)))
                .execute();
    }

    @Transactional
    @Override
    public List<Map<String, Object>> totalExpensesByDay(Long userId, LocalDate firstDate, LocalDate secondDate) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        return queryFactory
                .select(expense.date, expense.amount.sum())
                .from(expense)
                .where(expense.date.between(firstDate, secondDate).and(expense.users.id.eq(userId)))
                .groupBy(expense.date)
                .fetch().stream().map(tuple ->
                        new HashMap<String, Object>() {{
                            put("date", tuple.get(0, LocalDate.class));
                            put("total", tuple.get(1, Double.class));
                        }})
                .collect(Collectors.toList());
    }
}
