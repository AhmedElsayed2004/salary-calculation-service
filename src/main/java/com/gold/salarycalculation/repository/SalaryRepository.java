package com.gold.salarycalculation.repository;

import com.gold.salarycalculation.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findAllByMonthKey(String monthKey);
}
