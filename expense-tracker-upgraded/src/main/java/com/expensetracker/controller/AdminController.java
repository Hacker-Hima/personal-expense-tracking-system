package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.repository.*;
import com.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private ExpenseService expenseService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication auth) {
        Map<String,Object> stats = expenseService.getAdminStats();
        model.addAllAttributes(stats);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentUser", userRepository.findByUsername(auth.getName()).orElseThrow());
        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication auth) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentUser", userRepository.findByUsername(auth.getName()).orElseThrow());
        return "admin-users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User current = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (current.getId().equals(id)) {
            ra.addFlashAttribute("errorMsg", "You cannot delete your own account.");
        } else {
            userRepository.deleteById(id);
            ra.addFlashAttribute("successMsg", "User deleted successfully.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User current = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (current.getId().equals(id)) {
            ra.addFlashAttribute("errorMsg", "Cannot disable your own account.");
            return "redirect:/admin/users";
        }
        userRepository.findById(id).ifPresent(u -> {
            u.setEnabled(!u.isEnabled());
            userRepository.save(u);
        });
        ra.addFlashAttribute("successMsg", "User status updated.");
        return "redirect:/admin/users";
    }
}
