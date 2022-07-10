package com.adrian99.expensesManager.controllers;

import com.adrian99.expensesManager.emailVerification.EmailSender;
import com.adrian99.expensesManager.exception.ApiRequestException;
import com.adrian99.expensesManager.jwt.JwtConfig;
import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.model.Expense;
import com.adrian99.expensesManager.model.PayMethod;
import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.repositories.custom.implementation.ExpenseCustomRepositoryImpl;
import com.adrian99.expensesManager.services.ExpenseService;
import com.adrian99.expensesManager.services.UserService;
import com.adrian99.expensesManager.services.VerificationTokenService;
import com.adrian99.expensesManager.services.implementation.ApplicationUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
class UserControllerTest {

    @MockBean
    private ApplicationUserService applicationUserService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private SecretKey secretKey;

    @MockBean
    private JwtConfig jwtConfig;

    @MockBean
    private UserService userService;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private EmailSender emailSender;

    @MockBean
    private ExpenseCustomRepositoryImpl expenseCustomRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @WithMockUser(username = "adrian99")
    @Test
    void failWhenPostIncompleteExpense() throws Exception {

        mockMvc.perform(post("/api/users/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"20\"}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void failWhenPostNoUser() throws Exception {
        mockMvc.perform(post("/api/users/expenses"))
                .andExpect(status().isForbidden());

    }

    @WithMockUser(username = "adrian99")
    @Test
    void passWhenPostCompleteExpense() throws Exception {
        Expense expense = new Expense("Nice expense #2", Category.SCHOOL,
                LocalDate.of(2022, 6, 6),
                PayMethod.CARD, 20D, "Nice expense #2", null);

        when(userService.findByUsername("adrian99"))
                .thenReturn(new User("adrian99", "password", null,
                        null, "adrian@mail.com"));

        when(expenseService.save(any(Expense.class)))
                .thenReturn(expense);

        mockMvc.perform(post("/api/users/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "   \"amount\": 20,\n" +
                                "   \"category\": \"SCHOOL\",\n" +
                                "   \"date\" : \"2022-06-06\",\n" +
                                "   \"title\": \"Nice expense #2\",\n" +
                                "   \"payMethod\" : \"CARD\",\n" +
                                "   \"details\" : \"Nice expense #2\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(20))
                .andExpect(jsonPath("$.category").value("SCHOOL"))
                .andExpect(jsonPath("$.date").value("2022-06-06"))
                .andExpect(jsonPath("$.title").value("Nice expense #2"))
                .andExpect(jsonPath("$.payMethod").value("CARD"))
                .andExpect(jsonPath("$.details").value("Nice expense #2"));
    }

    @WithMockUser(username = "adrian99")
    @Test
    void passWhenDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "adrian99")
    @Test
    void failWhenDeleteUser() throws Exception {

        doThrow(new ApiRequestException("Something went wrong!")).when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void passWhenCreateUser() throws Exception {
        User expectedUser = new User("adrian99", "admin",
                true, "ROLE_USER", "adi@mail.com"
        );

        when(userService.findByEmail(any(String.class))).thenReturn(null);
        when(userService.findByUsername(any(String.class))).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("admin");
        when(userService.save(any(User.class))).thenReturn(expectedUser);

        mockMvc.perform(post("/api/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "   \"username\": \"adrian99\",\n" +
                                "   \"password\": \"admin\",\n" +
                                "   \"email\" : \"adi@mail.com\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("adrian99"))
                .andExpect(jsonPath("$.password").value("admin"))
                .andExpect(jsonPath("$.roles").value("ROLE_USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.email").value("adi@mail.com"));
    }

    @Test
    void failWhenCreatingUserWithExistingEmail() throws Exception {
        User user = new User("adrian99", "admin",
                true, "ROLE_USER", "adi@mail.com"
        );

        when(userService.findByEmail(any(String.class))).thenReturn(user);

        mockMvc.perform(post("/api/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "   \"username\": \"adrian99\",\n" +
                                "   \"password\": \"admin\",\n" +
                                "   \"email\" : \"adi@mail.com\"\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already used!"));
    }
    @Test
    void failWhenCreatingUserWithExistingUsername() throws Exception {
        User user = new User("adrian99", "admin",
                true, "ROLE_USER", "adi@mail.com"
        );

        when(userService.findByUsername(any(String.class))).thenReturn(user);

        mockMvc.perform(post("/api/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "   \"username\": \"adrian99\",\n" +
                                "   \"password\": \"admin\",\n" +
                                "   \"email\" : \"adi@mail.com\"\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists!"));
    }

    @WithMockUser(username = "adrian99")
    @Test
    void passWhenPostMultiplesExpenses() throws Exception {

        List<Expense> expenseList = List.of(
                new Expense("Nice expense #2", Category.SCHOOL,
                        LocalDate.of(2022, 6, 6),
                        PayMethod.CARD, 20D, "Nice expense #2", null),
                new Expense("Nice expense #3", Category.TRANSPORT,
                        LocalDate.of(2022, 7, 7),
                        PayMethod.CASH, 420D, "Nice expense #3", null));

        when(expenseService.saveAll(expenseList)).thenReturn(expenseList);

        mockMvc.perform(post("/api/users/multipleExpenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\n" +
                                "   \"amount\": 20,\n" +
                                "   \"category\": \"SCHOOL\",\n" +
                                "   \"date\" : \"2022-06-06\",\n" +
                                "   \"title\": \"Nice expense #2\",\n" +
                                "   \"payMethod\" : \"CARD\",\n" +
                                "   \"details\" : \"Nice expense #2\"\n" +
                                "},{"+
                                "   \"amount\": 420,\n" +
                                "   \"category\": \"TRANSPORT\",\n" +
                                "   \"date\" : \"2022-07-07\",\n" +
                                "   \"title\": \"Nice expense #3\",\n" +
                                "   \"payMethod\" : \"CASH\",\n" +
                                "   \"details\" : \"Nice expense #3\"\n" +
                                "}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].amount").value(20))
                .andExpect(jsonPath("$.[0].category").value("SCHOOL"))
                .andExpect(jsonPath("$.[0].date").value("2022-06-06"))
                .andExpect(jsonPath("$.[0].title").value("Nice expense #2"))
                .andExpect(jsonPath("$.[0].payMethod").value("CARD"))
                .andExpect(jsonPath("$.[0].details").value("Nice expense #2"))
                .andExpect(jsonPath("$.[1].amount").value(420))
                .andExpect(jsonPath("$.[1].category").value("TRANSPORT"))
                .andExpect(jsonPath("$.[1].date").value("2022-07-07"))
                .andExpect(jsonPath("$.[1].title").value("Nice expense #3"))
                .andExpect(jsonPath("$.[1].payMethod").value("CASH"))
                .andExpect(jsonPath("$.[1].details").value("Nice expense #3"));
    }

    @WithMockUser(username = "adrian99")
    @Test
    void failWhenPostMultiplesExpenses() throws Exception {
        mockMvc.perform(post("/api/users/multipleExpenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\n" +
                                "   \"amount\": 20\n" +
                                "},{"+
                                "   \"amount\": 420\n" +
                                "}]"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "adrian99")
    @Test
    void passWhenPutUserInfo() throws Exception {
        User user = new User("adrian99", "admin",
                true, "ROLE_USER", "adi@mail.com"
        );
        User expectedUser = new User("adrian99", "pass",
                true, "ROLE_USER", "adi@mail.com"
        );

        when(userService.findByUsername("adrian99")).thenReturn(user);
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);
        when(userService.save(user)).thenReturn(expectedUser);

        mockMvc.perform(put("/api/users/updateInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"newPassword\":\"pass\"," +
                                "\"password\":\"admin\"" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("adrian99"))
                .andExpect(jsonPath("$.password").value("pass"))
                .andExpect(jsonPath("$.roles").value("ROLE_USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.email").value("adi@mail.com"));
    }

}