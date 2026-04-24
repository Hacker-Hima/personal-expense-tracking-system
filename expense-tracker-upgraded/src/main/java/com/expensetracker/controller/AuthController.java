package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required=false) String error,
                            @RequestParam(required=false) String logout,
                            @RequestParam(required=false) String expired,
                            Model model) {
        if (error != null)   model.addAttribute("errorMsg", "Invalid username or password.");
        if (logout != null)  model.addAttribute("logoutMsg", "You have been logged out successfully.");
        if (expired != null) model.addAttribute("errorMsg", "Your session has expired. Please login again.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               RedirectAttributes ra, Model model) {
        if (result.hasErrors()) return "register";
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("errorMsg", "Username already taken. Choose another.");
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        userRepository.save(user);
        ra.addFlashAttribute("successMsg", "Registration successful! Please login.");
        return "redirect:/login";
    }

    @GetMapping("/")
    public String root() { return "redirect:/dashboard"; }
}
