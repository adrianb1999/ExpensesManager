package com.adrian99.expensesManager.controllers;

import com.adrian99.expensesManager.customQueries.SortBy;
import com.adrian99.expensesManager.customQueries.SortTypes;
import com.adrian99.expensesManager.emailVerification.EmailSender;
import com.adrian99.expensesManager.emailVerification.VerificationToken;
import com.adrian99.expensesManager.exception.ApiException;
import com.adrian99.expensesManager.exception.ApiRequestException;
import com.adrian99.expensesManager.model.*;
import com.adrian99.expensesManager.repositories.custom.implementation.ExpenseCustomRepositoryImpl;
import com.adrian99.expensesManager.services.ExpenseService;
import com.adrian99.expensesManager.services.UserService;
import com.adrian99.expensesManager.services.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final VerificationTokenService verificationTokenService;
    private final EmailSender emailSender;
    private final ExpenseCustomRepositoryImpl expenseCustomRepository;//delete later

    public UserController(PasswordEncoder passwordEncoder, UserService userService, ExpenseService expenseService, VerificationTokenService verificationTokenService, EmailSender emailSender, ExpenseCustomRepositoryImpl expenseCustomRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.expenseService = expenseService;
        this.verificationTokenService = verificationTokenService;
        this.emailSender = emailSender;
        this.expenseCustomRepository = expenseCustomRepository;
    }

    //Swagger example!
    @Operation(summary = "Get user expenses.",
            parameters = {
                    @Parameter(name = "Authorization", description = "Authorization-Token", required = true, in = ParameterIn.HEADER),
                    @Parameter(name = "Authorization-Cookie", description = "Authorization-Token", required = true, in = ParameterIn.COOKIE)

            },
            responses = {
                    @ApiResponse(description = "Get expenses success", responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Expense.class))))
            })

    @GetMapping("/api/users/expenses")
    public Map<String, Object> findUserExpenses(@RequestParam(name = "amount", required = false) Double amount,
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

        if (userService.findByEmail(user.getEmail()) != null)
            throw new ApiRequestException("Email already used!");

        if (userService.findByUsername(user.getUsername()) != null)
            throw new ApiRequestException("Username already exists!");

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setRoles("ROLE_USER");
        //TODO SET BACK TO FALSE
        newUser.setActive(true);

        userService.save(newUser);

        String token = userService.generateToken(newUser);
        //TODO uncomment
//        emailSender.sendEmail(newUser.getEmail(), token, ACCOUNT_ACTIVATION);

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
        User currentUser = userService.findByEmail(user.getEmail());

        if (currentUser == null)
            throw new ApiRequestException("The email doesn't exist!");
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
    public Expense editOrAddExpense(@RequestBody @Valid Expense newExpense, @PathVariable Long expenseId) {
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

    @DeleteMapping(value = "/api/users/expenses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteMultipleExpenses(@RequestParam(name = "expenseIds") Set<Long> idList, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());

        idList.forEach(aLong -> {
            if (expenseService.findById(aLong) == null) {
                throw new ApiRequestException("Expense with id " + aLong + " does not exist!");
            }
            if (expenseService.findById(aLong).getUsers() != currentUser) {
                throw new ApiRequestException("You cannot delete another user expense!");
            }
        });

        try {
            expenseService.deleteAllById(idList);
            return new ResponseEntity<>("{\"message\":\"Expenses deleted successfully\"", HttpStatus.OK);
        } catch (Exception e){
            throw new ApiRequestException("Something went wrong!");
        }
    }

    @PutMapping("/api/users/updateInfo")
    public User saveOrUpdate(@RequestBody Map<String, String> userInfo, Principal principal) {

        if (userInfo.get("password") == null)
            throw new ApiRequestException("Password is required!");

        User updateUser = userService.findByUsername(principal.getName());

        if (!passwordEncoder.matches(userInfo.get("password"), updateUser.getPassword()))
            throw new ApiRequestException("Password incorrect");

        if (userInfo.get("email") != null) {
            if (userInfo.get("email").isEmpty())
                throw new ApiRequestException("Email cannot be null!");

            if (Objects.equals(updateUser.getEmail(), userInfo.get("email"))) {
                throw new ApiRequestException("The email cannot be the same!");
            }

            if (userService.findByEmail(userInfo.get("email")) != null)
                throw new ApiRequestException("Email already used!");

            updateUser.setEmail(userInfo.get("email"));
        }

        if (userInfo.get("username") != null) {
            if (userInfo.get("username").isEmpty())
                throw new ApiRequestException("Username cannot be empty");

            if (Objects.equals(updateUser.getUsername(), userInfo.get("username")))
                throw new ApiRequestException("The username cannot be the same!");

            if (userService.findByUsername(userInfo.get("username")) != null)
                throw new ApiRequestException("Username already used!");

            updateUser.setUsername(userInfo.get("username"));
        }

        if (userInfo.get("newPassword") != null) {
            if (userInfo.get("newPassword").isEmpty())
                throw new ApiRequestException("Password cannot be empty");
            if (Objects.equals(updateUser.getPassword(), userInfo.get("newPassword")))
                throw new ApiRequestException("The password cannot be the same!");

            updateUser.setPassword(passwordEncoder.encode(userInfo.get("newPassword")));
        }

        return userService.save(updateUser);
    }

    @DeleteMapping(value = "/api/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUser(@PathVariable Long id, Principal principal) {
        log.debug("Entering delete user endpoint");

        //TODO check if is the same user or it is an ADMIN

        try {
            userService.deleteById(id);

            log.info("User with id=" + id + ", was deleted.");

            return new ResponseEntity<>("{\"message\":\"User deleted successfully\"", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Unable to delete user with id=" + id + ", message: " + e.getMessage());
            throw new ApiRequestException("Something went wrong!");
        }
    }

    @GetMapping("/api/users/statistics/lastWeekTotalPerDays")
    public List<Map<String, Object>> expensesByDay(Principal principal) {
        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseCustomRepository.totalExpensesByDay(userId, LocalDate.now().minusDays(6), LocalDate.now());
    }

    @GetMapping("/api/users/statistics/lastMonthsTotalPerMonthByCategory/{numOfMonths}")
    public List<Map<String, Object>> expensesByMonthByCategory(@PathVariable Integer numOfMonths, Principal principal) {
        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseCustomRepository.totalExpensesByLastNMonthsByCategory(userId, numOfMonths - 1);
    }

    @GetMapping("/api/users/statistics/dayAverage")
    public Map<String, Double> getDayAverage(Principal principal) {
        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseService.dayAverage(userId, LocalDate.now().minusMonths(6), LocalDate.now());
    }

    @GetMapping("/api/users/statistics/monthAverage")
    public Map<String, Double> getMonthAverage(Principal principal) {
        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseService.monthAverage(userId, LocalDate.now().minusMonths(6), LocalDate.now());
    }

    @GetMapping("/api/users/statistics/totalSpent")
    public Map<String, Object> getTotalSpent(Principal principal) {
        Long userId = userService.findByUsername(principal.getName()).getId();

        return expenseService.totalSpent(userId);
    }

    @PostMapping(value = "/api/changePassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> passwordBody, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (!Objects.equals(currentUser.getPassword(), passwordBody.get("oldPassword")))
            throw new ApiRequestException("Password is wrong!");

        currentUser.setPassword(passwordEncoder.encode(passwordBody.get("newPassword")));
        userService.save(currentUser);

        return new ResponseEntity<>("{\"message\":\"Password updated successfully!\"}", HttpStatus.OK);
    }

}
