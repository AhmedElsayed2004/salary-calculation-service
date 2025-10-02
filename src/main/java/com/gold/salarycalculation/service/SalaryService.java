package com.gold.salarycalculation.service;

import com.gold.salarycalculation.dto.SalaryCalculationResponse;
import com.gold.salarycalculation.entity.Salary;

import java.util.List;

public interface SalaryService {
    List<Salary> getSalariesByMonthKey(String monthKey);

    List<Salary> getSalariesByEmployeeId(Long employeeId);

    SalaryCalculationResponse processSalaryCalculation(String monthKey);
}
