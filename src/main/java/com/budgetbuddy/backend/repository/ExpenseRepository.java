package com.budgetbuddy.backend.repository;

import com.budgetbuddy.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser_Id(Long userId);
       List<Expense> findByUser_IdAndDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end
    );
  
}
