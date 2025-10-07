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
import java.util.*;
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

        // 1) employees + leaveRequests eager-loaded (your existing query)
        List<Employee> eligibleEmployees = employeeRepository.findActiveEmployeesEligibleForPayroll(monthKey, calculationDate);

        if (eligibleEmployees.isEmpty()) {
            return new SalaryCalculationResponse(monthKey, 0, BigDecimal.ZERO);
        }

        // 2) fetch existing salaries for this month but only for these employees
        List<Long> employeeIds = eligibleEmployees.stream().map(Employee::getId).toList();

        List<Salary> existingSalaries = salaryRepository.findAllByMonthKeyAndEmployee_IdIn(monthKey, employeeIds);

        Map<Long, List<Salary>> salariesByEmployeeId = existingSalaries.stream()
                .collect(Collectors.groupingBy(s -> s.getEmployee().getId()));

        // attach salaries to the already-loaded employees
        eligibleEmployees.forEach(emp ->
                emp.setSalaries(salariesByEmployeeId.getOrDefault(emp.getId(), new ArrayList<>()))
        );

        // 3) build and save
        List<Salary> salaries = buildSalaries(eligibleEmployees, monthKey, calculationDate);

        BigDecimal totalPayroll = salaries.stream()
                .map(Salary::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        salaryRepository.saveAll(salaries);

        return new SalaryCalculationResponse(monthKey, eligibleEmployees.size(), totalPayroll);
    }


    private List<Salary> buildSalaries(List<Employee> employees, String monthKey, LocalDate calculationDate) {
        return employees.stream()
                .map(e -> salaryCreator.create(e, monthKey, calculationDate))
                .toList();
    }

}
