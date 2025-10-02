package com.gold.salarycalculation.service;

import com.gold.salarycalculation.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SalaryCalculator {
    BigDecimal calculateBasicSalary(Employee employee, String monthKey, LocalDate calculationDate);

    BigDecimal calculateAllowances(Employee employee, LocalDate calculationDate);

    BigDecimal calculateDeduction(Employee employee, String monthKey, LocalDate calculationDate);

    BigDecimal calculatePerDaySalary(BigDecimal basicSalary, String monthKey);
}
