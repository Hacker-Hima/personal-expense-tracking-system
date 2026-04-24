package com.expensetracker.controller;

import com.expensetracker.model.*;
import com.expensetracker.repository.*;
import com.expensetracker.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ExpenseController {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ExpenseService expenseService;
    @Autowired private ExportService exportService;
    @Autowired private ArchivedExpenseRepository archivedExpenseRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth,
                            @RequestParam(required=false) String category,
                            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
                            @RequestParam(required=false) String keyword,
                            Model model) {
        User user = getUser(auth);
        List<Expense> expenses = expenseService.getFilteredExpenses(user, category, from, to, keyword);
        Map<String,Object> stats = expenseService.getDashboardData(user);

        model.addAllAttributes(stats);
        model.addAttribute("expenses", expenses);
        model.addAttribute("currentUser", user);
        model.addAttribute("newExpense", new Expense());
        model.addAttribute("categories", ExpenseService.DEFAULT_CATEGORIES);
        model.addAttribute("filterCategory", category);
        model.addAttribute("filterFrom", from);
        model.addAttribute("filterTo", to);
        model.addAttribute("filterKeyword", keyword);
        return "user-dashboard";
    }

    @PostMapping("/expenses/add")
    public String addExpense(@Valid @ModelAttribute("newExpense") Expense expense,
                             BindingResult result, Authentication auth,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("errorMsg", "Please fix form errors.");
            return "redirect:/dashboard";
        }
        User user = getUser(auth);
        expense.setUser(user);
        if (expense.getDate() == null) expense.setDate(LocalDate.now());
        expenseRepository.save(expense);
        ra.addFlashAttribute("successMsg", "Expense added successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense == null) { ra.addFlashAttribute("errorMsg", "Expense not found."); return "redirect:/dashboard"; }
        User user = getUser(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin || expense.getUser().getId().equals(user.getId())) {
            expenseRepository.delete(expense);
            ra.addFlashAttribute("successMsg", "Expense deleted.");
        } else {
            ra.addFlashAttribute("errorMsg", "Unauthorized.");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        model.addAttribute("currentUser", getUser(auth));
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required=false) BigDecimal salary,
                                @RequestParam(required=false) BigDecimal monthlyBudget,
                                Authentication auth, RedirectAttributes ra) {
        User user = getUser(auth);
        if (salary != null) user.setSalary(salary);
        if (monthlyBudget != null) user.setMonthlyBudget(monthlyBudget);
        userRepository.save(user);
        ra.addFlashAttribute("successMsg", "Profile updated!");
        return "redirect:/profile";
    }

    @PostMapping("/expenses/archive")
    public String archiveMonth(Authentication auth, RedirectAttributes ra) {
        expenseService.archiveMonthlyExpenses(getUser(auth));
        ra.addFlashAttribute("successMsg", "Last month's expenses archived successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/expenses/history")
    public String history(Authentication auth, Model model) {
        User user = getUser(auth);
        model.addAttribute("archivedExpenses", archivedExpenseRepository.findByUserOrderByDateDesc(user));
        model.addAttribute("currentUser", user);
        return "history";
    }

    @GetMapping("/expenses/export/csv")
    public void exportCsv(Authentication auth,
                          @RequestParam(required=false) String category,
                          @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                          @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
                          @RequestParam(required=false) String keyword,
                          HttpServletResponse response) throws IOException {
        User user = getUser(auth);
        List<Expense> expenses = expenseService.getFilteredExpenses(user, category, from, to, keyword);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");
        response.getOutputStream().write(exportService.exportToCsv(expenses));
    }

    @GetMapping("/expenses/export/pdf")
    public void exportPdf(Authentication auth,
                          @RequestParam(required=false) String category,
                          @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                          @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
                          @RequestParam(required=false) String keyword,
                          HttpServletResponse response) throws Exception {
        User user = getUser(auth);
        List<Expense> expenses = expenseService.getFilteredExpenses(user, category, from, to, keyword);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.pdf");
        response.getOutputStream().write(exportService.exportToPdf(expenses, user.getUsername()));
    }

    private User getUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
