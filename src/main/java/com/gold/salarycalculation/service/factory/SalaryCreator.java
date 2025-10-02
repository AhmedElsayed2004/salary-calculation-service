package com.gold.salarycalculation.service.factory;

import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.entity.Salary;
import com.gold.salarycalculation.service.SalaryCalculator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SalaryCreator {

    private final SalaryCalculator salaryCalculator;

    public SalaryCreator(SalaryCalculator salaryCalculator) {
        this.salaryCalculator = salaryCalculator;
    }

    public Salary create(Employee employee, String monthKey, LocalDate calculationDate) {
        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setMonthKey(monthKey);
        salary.setCalculationDate(calculationDate);
        salary.setBasicSalary(salaryCalculator.calculateBasicSalary(employee, monthKey, calculationDate));
        salary.setAllowances(salaryCalculator.calculateAllowances(employee, calculationDate));
        salary.setDeductions(salaryCalculator.calculateDeduction(employee, monthKey, calculationDate));
        salary.setNetSalary(salary.calculateNetSalary());
        Long salaryId = employee.getSalaries().isEmpty()
                ? null
                : employee.getSalaries().get(0).getId();
        salary.setId(salaryId);
        return salary;
    }
}
