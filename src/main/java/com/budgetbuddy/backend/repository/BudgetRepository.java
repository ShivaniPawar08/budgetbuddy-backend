package com.budgetbuddy.backend.repository;

import com.budgetbuddy.backend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUser_Id(Long userId);
}
