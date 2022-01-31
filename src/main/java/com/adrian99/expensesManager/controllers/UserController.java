package com.adrian99.expensesManager.controllers;

import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.model.*;
import com.adrian99.expensesManager.services.ExpenseService;
import com.adrian99.expensesManager.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final ExpenseService expenseService;

    public UserController(UserService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @GetMapping("/users/expenses")
    public List<Expense> findUserExpenses(@RequestParam(name = "amount", required = false) Double amount,
                                          @RequestParam(name = "amountLessThan", required = false) Double amountLessThan,
                                          @RequestParam(name = "amountGreaterThan", required = false) Double amountGreaterThan,
                                          @RequestParam(name = "date", required = false) String date,
                                          @RequestParam(name = "dateAfter", required = false) String dateAfter,
                                          @RequestParam(name = "dateBefore", required = false) String dateBefore,
                                          @RequestParam(name = "payMethod", required = false) PayMethod payMethod,
                                          @RequestParam(name = "category", required = false) Category category,
                                          @RequestParam(name = "sortBy", required = false) SortBy sortBy,
                                          @RequestParam(name = "sortType", required = false) SortTypes sortType,
                                          Principal principal) {
        Long id = userService.findByUsername(principal.getName()).getId();
        return expenseService.findAllByFilters(
                id,
                amount,
                amountLessThan,
                amountGreaterThan,
                date,
                dateAfter,
                dateBefore,
                payMethod,
                category,
                sortBy,
                sortType
        );
    }

    @PostMapping("/createUser")
    public User createUser(@RequestBody User user) {

        if(userService.findByUsername(user.getUsername()) != null)
            return null;

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setRoles("ROLE_USER");
        newUser.setActive(true);
        return userService.save(newUser);
    }

    @PostMapping("/users/expenses")
    public Expense addUserExpense(@RequestBody Expense expense, Principal principal) {

        User currentUser = userService.findByUsername(principal.getName());
        expense.setUsers(currentUser);

        return expenseService.save(expense);
    }

    @DeleteMapping("/users/expenses/{expenseId}")
    public void deleteUserExpense(@PathVariable Long expenseId, Principal principal) {
        Long userId = userService.findByUsername(principal.getName()).getId();
        expenseService.deleteByIdAndUserId(userId, expenseId);
    }

    @PutMapping("users/{id}")
    public User saveOrUpdate(@RequestBody User newUser, @PathVariable Long id) {
        User updateUser = userService.findById(id);

        if (updateUser == null) {
            newUser.setId(id);
            return userService.save(newUser);
        }
        newUser.setId(updateUser.getId());
        return userService.save(newUser);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
