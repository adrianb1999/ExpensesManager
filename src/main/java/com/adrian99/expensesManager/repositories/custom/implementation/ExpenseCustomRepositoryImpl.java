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
import java.util.List;

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
}
