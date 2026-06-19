package com.budgetbuddy.backend.controller;

import com.budgetbuddy.backend.repository.IncomeRepository;
import com.budgetbuddy.backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://shivani-pawar.onrender.com"
})
public class DashboardController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/balance")
    public Double getBalance(@RequestParam Long userId) {

        Double totalIncome = incomeRepository.findByUserId(userId)
                .stream()
                .mapToDouble(i -> i.getAmount())
                .sum();

        Double totalExpense = expenseRepository.findByUser_Id(userId)
                .stream()
                .mapToDouble(e -> e.getAmount())
                .sum();

        return totalIncome - totalExpense;
    }
}
