package com.budgetbuddy.backend.controller;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.budgetbuddy.backend.entity.Expense;
import com.budgetbuddy.backend.entity.User;
import com.budgetbuddy.backend.repository.ExpenseRepository;
import com.budgetbuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/expense")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://shivani-pawar.onrender.com"
})
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

  @PostMapping("/add")
public ResponseEntity<String> addExpense(
        @RequestBody Expense expense,
        @RequestParam Long userId) {

    if (expense.getTitle() == null || expense.getTitle().trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Title is required");
    }

    if (expense.getAmount() == null || expense.getAmount() <= 0) {
        return ResponseEntity.badRequest().body("Amount must be greater than 0");
    }

    if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Category is required");
    }

    User user = userRepository.findById(userId).orElse(null);

    if (user == null) {
        return ResponseEntity.badRequest().body("User not found");
    }

    expense.setUser(user);
    expense.setDate(LocalDate.now());

    expenseRepository.save(expense);

    return ResponseEntity.ok("Expense Added Successfully");
}
@GetMapping("/all")
public List<Expense> getExpenses(@RequestParam Long userId) {
    return expenseRepository.findByUser_Id(userId);
}
@DeleteMapping("/delete/{id}")
public String deleteExpense(@PathVariable Long id) {
    expenseRepository.deleteById(id);
    return "Expense Deleted Successfully";
}
@PutMapping("/update/{id}")
public String updateExpense(@PathVariable Long id, @RequestBody Expense newExpense) {

    Expense expense = expenseRepository.findById(id).orElse(null);

    if (expense == null) {
        return "Expense not found";
    }

    expense.setTitle(newExpense.getTitle());
    expense.setAmount(newExpense.getAmount());
    expense.setCategory(newExpense.getCategory());

    expenseRepository.save(expense);

    return "Expense Updated Successfully";
}
@GetMapping("/monthly-comparison")
public Map<String, Double> monthlyComparison(
        @RequestParam Long userId) {

    LocalDate today = LocalDate.now();

    LocalDate currentMonthStart =
            today.withDayOfMonth(1);

    LocalDate previousMonthStart =
            currentMonthStart.minusMonths(1);

    LocalDate previousMonthEnd =
            currentMonthStart.minusDays(1);

    double currentMonthTotal =
            expenseRepository
                    .findByUser_IdAndDateBetween(
                            userId,
                            currentMonthStart,
                            today
                    )
                    .stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

    double previousMonthTotal =
            expenseRepository
                    .findByUser_IdAndDateBetween(
                            userId,
                            previousMonthStart,
                            previousMonthEnd
                    )
                    .stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

    Map<String, Double> result = new HashMap<>();

    result.put("currentMonth", currentMonthTotal);
    result.put("previousMonth", previousMonthTotal);

    return result;
}
@GetMapping("/weekly-comparison")
public Map<String, Double> weeklyComparison(
        @RequestParam Long userId) {

    LocalDate today = LocalDate.now();

    LocalDate currentWeekStart =
            today.minusDays(6);

    LocalDate previousWeekStart =
            today.minusDays(13);

    LocalDate previousWeekEnd =
            today.minusDays(7);

    double currentWeekTotal =
            expenseRepository
                    .findByUser_IdAndDateBetween(
                            userId,
                            currentWeekStart,
                            today
                    )
                    .stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

    double previousWeekTotal =
            expenseRepository
                    .findByUser_IdAndDateBetween(
                            userId,
                            previousWeekStart,
                            previousWeekEnd
                    )
                    .stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

    Map<String, Double> result = new HashMap<>();

    result.put("currentWeek", currentWeekTotal);
    result.put("previousWeek", previousWeekTotal);

    return result;
}
@GetMapping("/category-summary")
public Map<String, Double> getCategorySummary(@RequestParam Long userId) {

    List<Expense> expenses =
            expenseRepository.findByUser_Id(userId);

    Map<String, Double> summary = new HashMap<>();

    for (Expense e : expenses) {
        String category = e.getCategory();
        Double amount = e.getAmount();

        summary.put(
                category,
                summary.getOrDefault(category, 0.0) + amount
        );
    }

    return summary;
}
@GetMapping("/monthly-trend")
public Map<String, Double> monthlyTrend(@RequestParam Long userId) {

    List<Expense> expenses =
            expenseRepository.findByUser_Id(userId);

    Map<String, Double> trend = new LinkedHashMap<>();

    LocalDate today = LocalDate.now();

    for (int i = 5; i >= 0; i--) {

        LocalDate monthDate = today.minusMonths(i);

        String month = monthDate.getMonth().name();

        double total = expenses.stream()
                .filter(e ->
                        e.getDate() != null &&
                        e.getDate().getMonth() == monthDate.getMonth() &&
                        e.getDate().getYear() == monthDate.getYear()
                )
                .mapToDouble(Expense::getAmount)
                .sum();

        trend.put(month.substring(0, 3), total);
    }

    return trend;
}
@GetMapping("/weekly-trend")
public Map<String, Double> weeklyTrend(@RequestParam Long userId) {

    List<Expense> expenses =
            expenseRepository.findByUser_Id(userId);

    Map<String, Double> map =
            new LinkedHashMap<>();

    map.put("Mon", 0.0);
    map.put("Tue", 0.0);
    map.put("Wed", 0.0);
    map.put("Thu", 0.0);
    map.put("Fri", 0.0);
    map.put("Sat", 0.0);
    map.put("Sun", 0.0);

    for (Expense e : expenses) {

        if (e.getDate() == null) continue;

        String day =
                e.getDate()
                 .getDayOfWeek()
                 .name()
                 .substring(0,1)
                 .toUpperCase()
                 +
                e.getDate()
                 .getDayOfWeek()
                 .name()
                 .substring(1,3)
                 .toLowerCase();

        map.put(
                day,
                map.getOrDefault(day, 0.0)
                        + e.getAmount()
        );
    }

    return map;
}
}