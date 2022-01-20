package com.adrian99.expensesManager.controllers;

import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.model.*;
import com.adrian99.expensesManager.services.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/expenses")
    public List<Expense> findAll(@RequestParam(name = "amount", required = false) Double amount,
                                 @RequestParam(name = "amountLessThan", required = false) Double amountLessThan,
                                 @RequestParam(name = "amountGreaterThan", required = false) Double amountGreaterThan,
                                 @RequestParam(name = "date", required = false) String date,
                                 @RequestParam(name = "dateAfter", required = false) String dateAfter,
                                 @RequestParam(name = "dateBefore", required = false) String dateBefore,
                                 @RequestParam(name = "payMethod", required = false) PayMethod payMethod,
                                 @RequestParam(name = "categories", required = false) List<String> categories,
                                 @RequestParam(name = "sortBy", required = false) SortBy sortBy,
                                 @RequestParam(name = "sortType", required = false) SortTypes sortType) {
        return expenseService.findAllByFilters(null,
                amount,
                amountLessThan,
                amountGreaterThan,
                date,
                dateAfter,
                dateBefore,
                payMethod,
                categories,
                sortBy,
                sortType);
    }

    @GetMapping("/expenses/{id}")
    public Expense findById(@PathVariable Long id) {
        return expenseService.findById(id);
    }

    @DeleteMapping("/expenses/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteById(id);
    }
}
