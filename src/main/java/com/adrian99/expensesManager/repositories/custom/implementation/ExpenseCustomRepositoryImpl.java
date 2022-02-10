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
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ExpenseCustomRepositoryImpl implements ExpenseCustomRepository {

    private final EntityManager em;

    public ExpenseCustomRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override  //TODO Find a better way
    public Map<String,Object> findAllByFilters(Long userId, Double amount, Double amountLessThan, Double amountGreaterThan, String date, String dateAfter, String dateBefore, PayMethod payMethod, Set<Category> category, SortBy sortBy, SortTypes sortType, Integer pageSize,Integer pageNum) {
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

        int daysInterval = Period.between(firstDate,secondDate).getDays() + 1;

        List<LocalDate> dateList = new ArrayList<>();
        for(int i = 0; i < daysInterval; i++)
            dateList.add(LocalDate.now().minusDays(i));

        List<Map<String, Object>> list = queryFactory
                .select(expense.date, expense.amount.sum())
                .from(expense)
                .where(expense.date.between(firstDate, secondDate).and(expense.users.id.eq(userId)))
                .groupBy(expense.date)
                .fetch().stream().map(tuple ->
                        new HashMap<String, Object>() {
                            {
                                put("date", tuple.get(0, LocalDate.class));
                                put("total", tuple.get(1, Double.class));
                                dateList.remove(tuple.get(0, LocalDate.class));
                            }
                        })
                .collect(Collectors.toList());
        for(int i = 0; i < dateList.size(); i++) {

            int finalI = i;
            list.add(((daysInterval - dateList.size() + i)- Period.between(dateList.get(i), LocalDate.now()).getDays()),
                    new HashMap<>() {
                        {
                            put("date", dateList.get(finalI));
                            put("total", Double.parseDouble("0"));
                        }
            });
        }
        return list;
    }

    @Override
    @Transactional
    public List<Map<String, Double>> totalExpensesByLastNMonths(Long userId, Integer numOfMonth) {

        LocalDate firstDate = LocalDate.now().minusMonths(numOfMonth);
        firstDate = firstDate.withDayOfMonth(1);
        LocalDate secondDate = LocalDate.now();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        Comparator<Map<String, Double>> mapComparator = (o1, o2) -> {
            if(Objects.equals(o1.get("year"), o2.get("year")))
                return o1.get("month").compareTo(o2.get("month"));
            return o1.get("year").compareTo(o2.get("year"));
        };

        List<Map<String, Double>> list = queryFactory
                .select(expense.date.month(), expense.date.year(), expense.amount.sum())
                .from(expense)
                .where(expense.date.between(firstDate, secondDate).and(expense.users.id.eq(userId)))
                .groupBy(expense.date.month(), expense.date.year())
                .fetch().stream().map(tuple ->
                        new HashMap<String, Double>() {
                            {
                                put("month",(double)tuple.get(0, Integer.class));
                                put("year",(double)tuple.get(1, Integer.class));
                                put("total", tuple.get(2, Double.class));
                            }
                        }).sorted(mapComparator)
                .collect(Collectors.toList());

        return list;
    }
}
