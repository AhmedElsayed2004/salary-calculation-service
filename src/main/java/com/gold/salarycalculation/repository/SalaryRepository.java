package com.gold.salarycalculation.repository;

import com.gold.salarycalculation.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findAllByMonthKey(String monthKey);

    List<Salary> findAllByEmployee_IdOrderByMonthKeyDesc(Long employeeId);

    Optional<Salary> findByEmployee_IdAndMonthKey(Long employeeId, String monthKey);

    boolean existsByEmployee_IdAndCalculationDate(Long employeeId, LocalDate calculationDate);
}
