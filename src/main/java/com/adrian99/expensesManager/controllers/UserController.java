package com.adrian99.expensesManager.controllers;

import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.emailVerification.EmailSender;
import com.adrian99.expensesManager.emailVerification.VerificationToken;
import com.adrian99.expensesManager.exception.ApiRequestException;
import com.adrian99.expensesManager.model.*;
import com.adrian99.expensesManager.repositories.custom.implementation.ExpenseCustomRepositoryImpl;
import com.adrian99.expensesManager.services.ExpenseService;
import com.adrian99.expensesManager.services.UserService;
import com.adrian99.expensesManager.services.VerificationTokenService;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adrian99.expensesManager.emailVerification.TokenType.*;

@RestController
public class UserController {

    private final UserService userService;
    private final ExpenseService expenseService;
    private final VerificationTokenService verificationTokenService;
    private final EmailSender emailSender;
    private final ExpenseCustomRepositoryImpl expenseCustomRepository;//delete later

    public UserController(UserService userService, ExpenseService expenseService, VerificationTokenService verificationTokenService, EmailSender emailSender, ExpenseCustomRepositoryImpl expenseCustomRepository) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.verificationTokenService = verificationTokenService;
        this.emailSender = emailSender;
        this.expenseCustomRepository = expenseCustomRepository;
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
    public User createUser(@RequestBody User user) throws MessagingException {

        if (userService.findByUsername(user.getUsername()) != null)
            throw new ApiRequestException("Username already exists!");
        if (user.getEmail() == null)
            throw new ApiRequestException("Email is null!");

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setRoles("ROLE_USER");
        newUser.setActive(false);

        userService.save(newUser);

        String token = userService.generateToken(newUser);

        emailSender.sendEmail(newUser.getEmail(), token, ACCOUNT_ACTIVATION);

        return newUser;
    }

    @GetMapping("/registrationConfirm")
    public void confirmUser(@RequestParam(name = "token") String token) {

        VerificationToken currentToken = verificationTokenService.isTokenValid(token);

        currentToken.getUser().setActive(true);

        verificationTokenService.deleteById(currentToken.getId());
    }

    @GetMapping("/passwordReset")
    public void sendResetToken(@RequestBody User user) throws MessagingException {

        User currentUser = userService.findByUsername(user.getUsername());

        if (currentUser == null)
            throw new ApiRequestException("The username doesn't exist!");
        if (!currentUser.getActive())
            throw new ApiRequestException("The account is not activated!");

        String token = userService.generateToken(currentUser);

        emailSender.sendEmail(currentUser.getEmail(), token, PASSWORD_RESET);
    }

    @PostMapping("/passwordReset")
    public void resetPassword(@RequestBody User user,
                              @RequestParam(name = "token") String token) {
        VerificationToken currentToken = verificationTokenService.isTokenValid(token);

        User currentUser = currentToken.getUser();
        currentUser.setPassword(user.getPassword());

        userService.save(currentUser);

        verificationTokenService.deleteById(currentToken.getId());
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

    @GetMapping("lastWeekTotalPerDays")
    public List<Map<String, Object>> expensesByDay(Principal principal) {

        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseCustomRepository.totalExpensesByDay(userId, LocalDate.now().minusDays(7),LocalDate.now());
    }

}
