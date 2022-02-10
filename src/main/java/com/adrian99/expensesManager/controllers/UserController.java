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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.adrian99.expensesManager.emailVerification.TokenType.*;

@RestController
@Validated
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

    @GetMapping("/api/users/expenses")
    public Map<String,Object> findUserExpenses(@RequestParam(name = "amount", required = false) Double amount,
                                          @RequestParam(name = "amountLessThan", required = false) Double amountLessThan,
                                          @RequestParam(name = "amountGreaterThan", required = false) Double amountGreaterThan,
                                          @RequestParam(name = "date", required = false) String date,
                                          @RequestParam(name = "dateAfter", required = false) String dateAfter,
                                          @RequestParam(name = "dateBefore", required = false) String dateBefore,
                                          @RequestParam(name = "payMethod", required = false) PayMethod payMethod,
                                          @RequestParam(name = "category", required = false) Set<Category> category,
                                          @RequestParam(name = "sortBy", required = false) SortBy sortBy,
                                          @RequestParam(name = "sortType", required = false) SortTypes sortType,
                                          @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                          @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                          Principal principal) {
        Long id = userService.findByUsername(principal.getName()).getId();
        Map<String, Object> expenses = expenseService.findAllByFilters(
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
                sortType,
                pageSize,
                pageNum
        );
        return expenses;
    }

    @PostMapping("/api/createUser")
    public User createUser(@RequestBody @Valid User user) throws MessagingException {

        if(userService.findByEmail(user.getEmail()) != null)
            throw new ApiRequestException("Email already used!");

        if (userService.findByUsername(user.getUsername()) != null)
            throw new ApiRequestException("Username already exists!");

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

    @GetMapping("/api/registrationConfirm")
    public void confirmUser(@RequestParam(name = "token") String token) {
        VerificationToken currentToken = verificationTokenService.isTokenValid(token);

        currentToken.getUser().setActive(true);

        verificationTokenService.deleteById(currentToken.getId());
    }

    @PostMapping("/api/passwordResetSendLink")
    public void sendResetToken(@RequestBody User user) throws MessagingException {

        User currentUser = userService.findByUsername(user.getUsername());

        if (currentUser == null)
            throw new ApiRequestException("The username doesn't exist!");
        if (!currentUser.getActive())
            throw new ApiRequestException("The account is not activated!");

        String token = userService.generateToken(currentUser);

        emailSender.sendEmail(currentUser.getEmail(), token, PASSWORD_RESET);
    }

    @PostMapping("/api/passwordReset")
    public void resetPassword(@RequestBody User user,
                              @RequestParam(name = "token") String token) {
        VerificationToken currentToken = verificationTokenService.isTokenValid(token);

        User currentUser = currentToken.getUser();
        currentUser.setPassword(user.getPassword());

        userService.save(currentUser);

        verificationTokenService.deleteById(currentToken.getId());
    }

    @PostMapping("/api/users/multipleExpenses")
    public Iterable<Expense> addUserAllExpenses(@RequestBody @Valid List<Expense> expense, Principal principal) {

        User currentUser = userService.findByUsername(principal.getName());

        expense.forEach(expense1 -> expense1.setUsers(currentUser));

        return expenseService.saveAll(expense);
    }

    @PostMapping("/api/users/expenses")
    public Expense addUserExpense(@RequestBody @Valid Expense expense, Principal principal) {

        User currentUser = userService.findByUsername(principal.getName());
        expense.setUsers(currentUser);

        return expenseService.save(expense);
    }

    @PutMapping("/api/users/expenses/{expenseId}")
    public Expense editOrAddExpense(@RequestBody @Valid Expense newExpense, @PathVariable Long expenseId){
        Expense currentExpense = expenseService.findById(expenseId);

        newExpense.setId(currentExpense.getId());
        newExpense.setUsers(currentExpense.getUsers());

        return expenseService.save(newExpense);
    }

    @DeleteMapping("/api/users/expenses/{expenseId}")
    public void deleteUserExpense(@PathVariable Long expenseId, Principal principal) {
        Long userId = userService.findByUsername(principal.getName()).getId();
        expenseService.deleteByIdAndUserId(userId, expenseId);
    }

    @DeleteMapping("/api/users/expenses")
    public void deleteMultipleExpenses(@RequestParam(name = "expenseIds") Set<Long> idList){
        idList.forEach(aLong -> {
            if(expenseService.findById(aLong) == null) {
                System.out.println("Exista deja id = " + aLong);
                throw new ApiRequestException("Expense with id " + aLong + " does not exist!");
            }
        });
        expenseService.deleteAllById(idList);
    }

    @PutMapping("/api/users")
    public User saveOrUpdate(@RequestBody User newUser, Principal principal) {
        User updateUser = userService.findByUsername(principal.getName());

        if (updateUser != null) {
           if(newUser.getEmail() != null){
               if(newUser.getEmail().isEmpty())
                   throw new ApiRequestException("Email cannot be null!");

               if(userService.findByEmail(newUser.getEmail()) != null)
                   throw new ApiRequestException("Email already used!");

               if(Objects.equals(updateUser.getEmail(), newUser.getEmail())) {
                   throw new ApiRequestException("The email cannot be the same!");
               }
               updateUser.setEmail(newUser.getEmail());
           }

           if(newUser.getUsername() != null){
               if(newUser.getUsername().isEmpty())
                   throw new ApiRequestException("Username cannot be empty");

               if(userService.findByUsername(newUser.getUsername()) != null)
                   throw new ApiRequestException("Username already used!");

               if(Objects.equals(updateUser.getUsername(),newUser.getUsername()))
                   throw new ApiRequestException("The username cannot be the same!");
               updateUser.setUsername(newUser.getUsername());
           }

           if(newUser.getPassword() != null) {
               if(newUser.getPassword().isEmpty())
                   throw new ApiRequestException("Password cannot be empty");
               updateUser.setPassword(newUser.getPassword());
           }
           return userService.save(updateUser);
        }

        newUser.setId(updateUser.getId());
        return userService.save(newUser);
    }

    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping("/api/lastWeekTotalPerDays")
    public List<Map<String, Object>> expensesByDay(Principal principal) {

        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseCustomRepository.totalExpensesByDay(userId, LocalDate.now().minusDays(6), LocalDate.now());
    }
    @GetMapping("/api/lastMonthsTotalPerMonth")
    public List<Map<String, Double>> expensesByMonth(Principal principal) {

        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseCustomRepository.totalExpensesByLastNMonths(userId, 11);
    }
}
