package com.expensetracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "archived_expenses")
public class ArchivedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String category;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int archiveMonth;

    @Column(nullable = false)
    private int archiveYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ArchivedExpense() {}

    public ArchivedExpense(Expense expense) {
        this.amount = expense.getAmount();
        this.category = expense.getCategory();
        this.description = expense.getDescription();
        this.date = expense.getDate();
        this.user = expense.getUser();
        if (expense.getDate() != null) {
            this.archiveMonth = expense.getDate().getMonthValue();
            this.archiveYear = expense.getDate().getYear();
        }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getArchiveMonth() { return archiveMonth; }
    public void setArchiveMonth(int archiveMonth) { this.archiveMonth = archiveMonth; }
    public int getArchiveYear() { return archiveYear; }
    public void setArchiveYear(int archiveYear) { this.archiveYear = archiveYear; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
