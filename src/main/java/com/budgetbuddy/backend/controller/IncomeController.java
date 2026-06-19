package com.budgetbuddy.backend.controller;

import com.budgetbuddy.backend.entity.Income;
import com.budgetbuddy.backend.entity.User;
import com.budgetbuddy.backend.repository.IncomeRepository;
import com.budgetbuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/income")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://shivani-pawar.onrender.com"
})
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public String addIncome(@RequestBody Income income, @RequestParam Long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return "User not found";
        }

        income.setUser(user);
        income.setDate(LocalDate.now());

        incomeRepository.save(income);

        return "Income Added Successfully";
    }
}