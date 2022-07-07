package com.adrian99.expensesManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.checkerframework.common.value.qual.DoubleVal;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title cannot be empty")
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Category cannot be empty")
    private Category category;

    @NotNull(message = "Date cannot be empty")
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "PayMethod cannot be empty")
    private PayMethod payMethod;

    @NotNull(message = "Amount cannot be empty")
    private Double amount;

    private String details;

    @JsonIgnore
    @ManyToOne
    private User users;

    public Expense() {
    }

    public Expense(String title, Category category, LocalDate date, PayMethod payMethod, Double amount, String details, User users) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.payMethod = payMethod;
        this.amount = amount;
        this.details = details;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayMethod payMethod) {
        this.payMethod = payMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id) && Objects.equals(title, expense.title) && category == expense.category && Objects.equals(date, expense.date) && payMethod == expense.payMethod && Objects.equals(amount, expense.amount) && Objects.equals(details, expense.details) && Objects.equals(users, expense.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, category, date, payMethod, amount, details, users);
    }
}
