package com.adrian99.expensesManager.repositories;

import com.adrian99.expensesManager.PostgresTestContainer;
import com.adrian99.expensesManager.customQueries.ExpenseQueries;
import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.model.PayMethod;
import com.adrian99.expensesManager.model.QExpense;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.spliterator;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExpenseRepositoryTest extends PostgresTestContainer {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    @Sql("/add_expenses.sql")
    void passWhenFindById() {
        Expense expectedExpense = new Expense("Exp. no. 1", Category.FUEL,
                LocalDate.of(2021,6,25), PayMethod.CARD,240D,
                "Exp. no. 1",null);
        expectedExpense.setId(1L);

        Expense currentExpense = expenseRepository.findById(1L).orElse(null);

        assertThat(currentExpense).usingRecursiveComparison()
                .ignoringFields("userId").isEqualTo(expectedExpense);
    }

    @Test
    @Sql("/add_expenses.sql")
    void failWhenFindById() {
        Expense currentExpense = expenseRepository.findById(21L).orElse(null);
        assertThat(currentExpense).isEqualTo(null);
    }

    @Test
    @Sql("/add_expenses.sql")
    void passWhenFindAll(){
        List<Expense> expenseList = (List<Expense>) expenseRepository.findAll();

        assertThat(expenseList.size()).isEqualTo(20);
    }

    @Test
    @Sql("/add_expenses.sql")
    void passWhenFindAllByCategory(){

        QExpense qExpense = QExpense.expense;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.or(qExpense.category.eq(Category.SCHOOL));

        List<Expense> expenseList = (List<Expense>) expenseRepository.findAll(booleanBuilder);

        assertThat(expenseList.size()).isEqualTo(2);
    }

    @Test
    @Sql("/add_expenses.sql")
    void passWhenFindAllByPayMethod(){

        QExpense qExpense = QExpense.expense;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.or(qExpense.payMethod.eq(PayMethod.CARD));

        List<Expense> expenseList = (List<Expense>) expenseRepository.findAll(booleanBuilder);

        assertThat(expenseList.size()).isEqualTo(7);
    }

    @Test
    void passWhenSaveExpense(){
        Expense expectedExpense = new Expense("Exp. no. 1", Category.FUEL,
                LocalDate.of(2021,6,25), PayMethod.CARD,240D,
                "Exp. no. 1",null);

        Expense expense = expenseRepository.save(expectedExpense);

        assertThat(expense).usingRecursiveComparison()
                .ignoringFields("id", "userId").isEqualTo(expectedExpense);
    }

    @Test
    @Sql("/add_expenses.sql")
    void passWhenEditExpense(){
        Expense expectedExpense = new Expense("Exp. no. 1", Category.FUEL,
                LocalDate.of(2021,6,25), PayMethod.CARD,240D,
                "Exp. no. 1",null);
        expectedExpense.setId(1L);

        Expense expense = expenseRepository.save(expectedExpense);

        assertThat(expense).usingRecursiveComparison().isEqualTo(expectedExpense);
    }

}