package com.adrian99.expensesManager.repositories.custom.implementation;

import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.model.Category;

import com.adrian99.expensesManager.model.PayMethod;
import com.adrian99.expensesManager.model.QExpense;
import com.adrian99.expensesManager.repositories.custom.ExpenseCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public Map<String, Object> findAllByFilters(Long userId, Double amount, Double amountLessThan, Double amountGreaterThan, String date, String dateAfter, String dateBefore, PayMethod payMethod, Set<Category> category, SortBy sortBy, SortTypes sortType, Integer pageSize, Integer pageNum) {
        return null;
    }

    @Transactional
    @Override
    public void deleteByIdAndUserId(Long userId, Long expenseId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        queryFactory.delete(expense)
                .where(
                        expense.id.eq(expenseId)
                                .and(expense.users.id.eq(userId)))
                .execute();
    }

    @Transactional
    @Override
    public List<Map<String, Object>> totalExpensesByDay(Long userId, LocalDate firstDate, LocalDate secondDate) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        int daysInterval = Period.between(firstDate, secondDate).getDays() + 1;

        List<LocalDate> dateList = new ArrayList<>();
        for (int i = 0; i < daysInterval; i++)
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

        if(list.size() == 0)
            return null;

        for (int i = 0; i < dateList.size(); i++) {

            int finalI = i;
            list.add(((daysInterval - dateList.size() + i) - Period.between(dateList.get(i), LocalDate.now()).getDays()),
                    Map.of(
                            "date", dateList.get(finalI),
                            "total", Double.parseDouble("0"))
            );
        }
        return list;
    }

    public List<Map<String, Object>> totalExpensesByLastNMonthsByCategory(Long userId, Integer numOfMonth) {
        LocalDate firstDate = LocalDate.now().minusMonths(numOfMonth);
        firstDate = firstDate.withDayOfMonth(1);
        LocalDate secondDate = LocalDate.now();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        Comparator<Tuple> listComparator = Comparator.comparing(o -> o.get(0, Integer.class));

        List<Tuple> fetch = queryFactory
                .select(expense.date.yearMonth(), expense.category, expense.amount.sum())
                .from(expense)
                .where(expense.date.between(firstDate, secondDate).and(expense.users.id.eq(userId)))
                .groupBy(expense.date.yearMonth(), expense.category)
                .fetch().stream().sorted(listComparator).collect(Collectors.toList());

        if(fetch.size() == 0)
            return null;

        List<Map<String, Object>> mapList = new ArrayList<>();
        Double totalMonth;

        for (int i = 0; i < fetch.size(); i++) {
            totalMonth = 0D;
            Map<String, Object> currentMap = new HashMap<>();
            Integer currentYearMonth = fetch.get(i).get(0, Integer.class);

            currentMap.put("year", currentYearMonth / 100);
            currentMap.put("month", currentYearMonth % 100);
            Map<Category, Double> categoryDoubleMap = new HashMap<>();

            while (Objects.equals(fetch.get(i).get(0, Integer.class), currentYearMonth)) {

                totalMonth += fetch.get(i).get(2, Double.class);
                categoryDoubleMap.put(fetch.get(i).get(1, Category.class), fetch.get(i).get(2, Double.class));

                if (i + 1 < fetch.size())
                    i++;
                else
                    break;
                if (i == fetch.size() - 1)
                    break;
            }

            currentMap.put("total", totalMonth);
            currentMap.put("categories", categoryDoubleMap);
            mapList.add(currentMap);
            if (i == fetch.size() - 1)
                break;
            i--;
        }

        return mapList;
    }

    @Transactional
    @Override
    public Map<String, Double> dayAverage(Long userId, LocalDate firstDate, LocalDate secondDate) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        Double averageExpense = BigDecimal.valueOf(queryFactory
                        .select(expense.amount.avg())
                        .from(expense)
                        .where(expense.date.between(firstDate, secondDate))
                        .fetchFirst())
                .setScale(2, RoundingMode.CEILING).doubleValue();

        return Map.of(
                "average", averageExpense
        );
    }

    @Transactional
    @Override
    public Map<String, Double> monthAverage(Long userId, LocalDate firstDate, LocalDate secondDate) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        firstDate = firstDate.withDayOfMonth(1);

        double sum = 0;

        List<Tuple> fetch = queryFactory
                .select(expense.date.month(), expense.amount.sum())
                .from(expense)
                .where(expense.date.between(firstDate, secondDate).and(expense.users.id.eq(userId)))
                .groupBy(expense.date.month())
                .fetch();
        for (Tuple tuple : fetch) {
            sum += tuple.get(1, Double.class);
        }
        sum = sum / fetch.size();

        Double averageExpense = BigDecimal.valueOf(sum)
                .setScale(2, RoundingMode.CEILING).doubleValue();

        return Map.of(
                "average", averageExpense
        );
    }

    @Override
    public Map<String, Object> totalSpent(Long userId) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QExpense expense = QExpense.expense;

        Map<Category, Object> categories = new HashMap<>();
        Double total = 0D;
        List<Tuple> fetch = queryFactory
                .select(expense.category, expense.amount.sum())
                .from(expense)
                .where(expense.users.id.eq(userId))
                .groupBy(expense.category)
                .fetch();

        for(int i = 0; i < fetch.size(); i++){
            categories.put(fetch.get(i).get(0, Category.class), fetch.get(i).get(1, Double.class));
            total += fetch.get(i).get(1, Double.class);
        }
        return Map.of(
                "total",total,
                "categories", categories
        );
    }
}
