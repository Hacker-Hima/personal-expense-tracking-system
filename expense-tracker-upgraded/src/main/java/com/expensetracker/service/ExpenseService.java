package com.expensetracker.service;

import com.expensetracker.model.*;
import com.expensetracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExpenseService {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private ArchivedExpenseRepository archivedExpenseRepository;
    @Autowired private UserRepository userRepository;

    public static final List<String> DEFAULT_CATEGORIES = List.of(
        "Food & Dining", "Transport", "Housing & Rent", "Healthcare",
        "Entertainment", "Shopping", "Education", "Utilities",
        "Travel", "Savings & Investment", "Personal Care", "Other"
    );

    public List<Expense> getFilteredExpenses(User user, String category, LocalDate from, LocalDate to, String keyword) {
        String cat = (category != null && category.isBlank()) ? null : category;
        String kw = (keyword != null && keyword.isBlank()) ? null : keyword;
        return expenseRepository.filterExpenses(user, cat, from, to, kw);
    }

    public Map<String, Object> getDashboardData(User user) {
        Map<String, Object> data = new HashMap<>();

        BigDecimal totalExpenses = expenseRepository.sumAmountByUser(user);
        totalExpenses = totalExpenses != null ? totalExpenses : BigDecimal.ZERO;

        BigDecimal salary = user.getSalary() != null ? user.getSalary() : BigDecimal.ZERO;
        BigDecimal budget = user.getMonthlyBudget() != null ? user.getMonthlyBudget() : BigDecimal.ZERO;

        // Current month expenses
        LocalDate now = LocalDate.now();
        BigDecimal monthExpenses = expenseRepository.sumByUserAndMonth(user, now.getMonthValue(), now.getYear());
        monthExpenses = monthExpenses != null ? monthExpenses : BigDecimal.ZERO;

        BigDecimal remaining = salary.subtract(monthExpenses);
        BigDecimal budgetRemaining = budget.subtract(monthExpenses);

        // Budget warning: 80% threshold
        boolean budgetWarning = false;
        double budgetPercent = 0;
        if (budget.compareTo(BigDecimal.ZERO) > 0) {
            budgetPercent = monthExpenses.divide(budget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            budgetWarning = budgetPercent >= 80;
        }

        // Category breakdown for chart
        List<Object[]> catData = expenseRepository.sumByCategory(user);
        Map<String, BigDecimal> categoryMap = new LinkedHashMap<>();
        for (Object[] row : catData) {
            categoryMap.put((String) row[0], (BigDecimal) row[1]);
        }

        // Monthly trend (current year)
        List<Object[]> monthlyData = expenseRepository.sumByMonthAndYear(user, now.getYear());
        BigDecimal[] monthlyAmounts = new BigDecimal[12];
        Arrays.fill(monthlyAmounts, BigDecimal.ZERO);
        for (Object[] row : monthlyData) {
            int month = ((Number) row[0]).intValue();
            monthlyAmounts[month - 1] = (BigDecimal) row[1];
        }

        data.put("totalExpenses", totalExpenses);
        data.put("monthExpenses", monthExpenses);
        data.put("salary", salary);
        data.put("budget", budget);
        data.put("remaining", remaining);
        data.put("budgetRemaining", budgetRemaining);
        data.put("budgetWarning", budgetWarning);
        data.put("budgetPercent", budgetPercent);
        data.put("categoryMap", categoryMap);
        data.put("monthlyAmounts", monthlyAmounts);
        data.put("count", expenseRepository.countByUser(user));

        // High spending category alert
        if (!categoryMap.isEmpty()) {
            Map.Entry<String, BigDecimal> topCategory = categoryMap.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);
            if (topCategory != null && budget.compareTo(BigDecimal.ZERO) > 0) {
                double catPercent = topCategory.getValue().divide(budget, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
                if (catPercent >= 30) {
                    data.put("highSpendCategory", topCategory.getKey());
                    data.put("highSpendCategoryAmount", topCategory.getValue());
                }
            }
        }

        return data;
    }

    @Transactional
    public void archiveMonthlyExpenses(User user) {
        LocalDate now = LocalDate.now();
        int prevMonth = now.minusMonths(1).getMonthValue();
        int prevYear = now.minusMonths(1).getYear();

        LocalDate from = LocalDate.of(prevYear, prevMonth, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        List<Expense> toArchive = expenseRepository.findByUserAndDateBetweenOrderByDateDesc(user, from, to);
        for (Expense e : toArchive) {
            ArchivedExpense archived = new ArchivedExpense(e);
            archivedExpenseRepository.save(archived);
            expenseRepository.delete(e);
        }
        user.setLastResetDate(now);
        userRepository.save(user);
    }

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        BigDecimal totalAll = expenseRepository.sumAllAmounts();
        stats.put("totalAll", totalAll != null ? totalAll : BigDecimal.ZERO);
        stats.put("totalExpenseCount", expenseRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("topSpenders", expenseRepository.findTopSpenders());
        stats.put("allExpenses", expenseRepository.findAllByOrderByDateDesc());
        return stats;
    }
}
