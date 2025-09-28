package com.gold.salarycalculation.service;

import com.gold.salarycalculation.dto.SalaryCalculationResponse;
import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.entity.Salary;
import com.gold.salarycalculation.enums.EmployeeStatus;
import com.gold.salarycalculation.repository.EmployeeRepository;
import com.gold.salarycalculation.repository.SalaryRepository;
import com.gold.salarycalculation.service.factory.SalaryFactory;
import com.gold.salarycalculation.service.util.CalculationDateResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryFactory salaryFactory;
    private final CalculationDateResolver calculationDateResolver;

    public SalaryServiceImpl(SalaryRepository salaryRepository
            , EmployeeRepository employeeRepository
            , SalaryFactory salaryFactory
            , CalculationDateResolver calculationDateResolver) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
        this.salaryFactory = salaryFactory;
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

        List<Employee> activeEmployees = employeeRepository.findAllByStatusAndJoinDateLessThanEqual(EmployeeStatus.ACTIVE, calculationDate);

        List<Salary> salaries = buildSalaries(activeEmployees, monthKey, calculationDate);

        BigDecimal totalPayroll = salaries.stream()
                .map(this::saveSalary)
                .map(Salary::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        return new SalaryCalculationResponse(monthKey, activeEmployees.size(), totalPayroll);
    }


    private Salary saveSalary(Salary salary) {
        Optional<Salary> oldSalary = salaryRepository.findByEmployee_IdAndMonthKey(salary.getEmployee().getId(), salary.getMonthKey());
        oldSalary.ifPresent(value -> salary.setId(value.getId()));
        return salaryRepository.save(salary);
    }


    private List<Salary> buildSalaries(List<Employee> employees, String monthKey, LocalDate calculationDate) {
        return employees.stream()
                .filter(e -> !salaryRepository.existsByEmployee_IdAndCalculationDate(e.getId(), calculationDate))
                .map(e -> salaryFactory.create(e, monthKey, calculationDate))
                .toList();
    }

}
