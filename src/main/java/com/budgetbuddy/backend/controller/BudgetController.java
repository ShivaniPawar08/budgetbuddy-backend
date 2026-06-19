package com.budgetbuddy.backend.controller;

import com.budgetbuddy.backend.entity.Budget;
import com.budgetbuddy.backend.entity.User;
import com.budgetbuddy.backend.repository.BudgetRepository;
import com.budgetbuddy.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/budget")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://shivani-pawar.onrender.com"
})
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/set")
    public String setBudget(
            @RequestParam Long userId,
            @RequestParam Double amount) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return "User not found";
        }

        Optional<Budget> existingBudget =
                budgetRepository.findByUser_Id(userId);

        if (existingBudget.isPresent()) {

            Budget budget = existingBudget.get();
            budget.setAmount(amount);

            budgetRepository.save(budget);

        } else {

            Budget budget = new Budget();
            budget.setAmount(amount);
            budget.setUser(user);

            budgetRepository.save(budget);
        }

        return "Budget Saved Successfully";
    }

    @GetMapping("/get")
    public Double getBudget(@RequestParam Long userId) {

        Optional<Budget> budget =
                budgetRepository.findByUser_Id(userId);

        return budget.map(Budget::getAmount).orElse(0.0);
    }
}