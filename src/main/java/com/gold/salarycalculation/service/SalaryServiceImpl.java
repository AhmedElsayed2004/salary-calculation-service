package com.gold.salarycalculation.service;

import com.gold.salarycalculation.dto.SalaryCalculationResponse;
import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.entity.Salary;
import com.gold.salarycalculation.enums.EmployeeStatus;
import com.gold.salarycalculation.repository.EmployeeRepository;
import com.gold.salarycalculation.repository.SalaryRepository;
import com.gold.salarycalculation.service.factory.SalaryCreator;
import com.gold.salarycalculation.service.util.CalculationDateResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryCreator salaryCreator;
    private final CalculationDateResolver calculationDateResolver;

    public SalaryServiceImpl(SalaryRepository salaryRepository,
                             EmployeeRepository employeeRepository,
                             SalaryCreator salaryCreator,
                             CalculationDateResolver calculationDateResolver) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
        this.salaryCreator = salaryCreator;
        this.calculationDateResolver = calculationDateResolver;
    }

    public List<Salary> getSalariesByMonthKey(String monthKey) {
        return salaryRepository.findAllByMonthKey(monthKey);
    }

    public List<Salary> getSalariesByEmployeeId(Long employeeId) {
        return salaryRepository.findAllByEmployee_IdOrderByMonthKeyDesc(employeeId);
    }

    public SalaryCalculationResponse processSalaryCalculation(String monthKey) {

        LocalDate calculationDate = calculationDateResolver.resolve(monthKey);

        List<Employee> activeEmployees = employeeRepository.findEligibleForPayroll(monthKey, calculationDate);

        List<Salary> salaries = buildSalaries(activeEmployees, monthKey, calculationDate);

        BigDecimal totalPayroll = salaries.stream()
                .map(Salary::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        salaryRepository.saveAll(salaries);

        return new SalaryCalculationResponse(monthKey, activeEmployees.size(), totalPayroll);
    }


    private List<Salary> buildSalaries(List<Employee> employees, String monthKey, LocalDate calculationDate) {
        return employees.stream()
                .map(e -> salaryCreator.create(e, monthKey, calculationDate))
                .toList();
    }

}
