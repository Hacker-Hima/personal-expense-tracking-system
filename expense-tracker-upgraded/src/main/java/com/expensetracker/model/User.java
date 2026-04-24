package com.expensetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ROLE_USER or ROLE_ADMIN

    @Column(nullable = false)
    private boolean enabled = true;

    // Salary & Budget
    @Column(precision = 12, scale = 2)
    private BigDecimal salary = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal monthlyBudget = BigDecimal.ZERO;

    // Monthly reset tracking
    @Column
    private LocalDate lastResetDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ArchivedExpense> archivedExpenses;

    // Constructors
    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public BigDecimal getSalary() { return salary != null ? salary : BigDecimal.ZERO; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public BigDecimal getMonthlyBudget() { return monthlyBudget != null ? monthlyBudget : BigDecimal.ZERO; }
    public void setMonthlyBudget(BigDecimal monthlyBudget) { this.monthlyBudget = monthlyBudget; }
    public LocalDate getLastResetDate() { return lastResetDate; }
    public void setLastResetDate(LocalDate lastResetDate) { this.lastResetDate = lastResetDate; }
    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
    public List<ArchivedExpense> getArchivedExpenses() { return archivedExpenses; }
    public void setArchivedExpenses(List<ArchivedExpense> archivedExpenses) { this.archivedExpenses = archivedExpenses; }
}
