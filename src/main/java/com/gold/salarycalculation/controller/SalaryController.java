package com.gold.salarycalculation.controller;

import com.gold.salarycalculation.dto.SalaryCalculationResponse;
import com.gold.salarycalculation.entity.Salary;
import com.gold.salarycalculation.exception.EmployeeNotFoundException;
import com.gold.salarycalculation.service.EmployeeService;
import com.gold.salarycalculation.service.SalaryService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/salaries")
public class SalaryController {
    private final SalaryService salaryService;
    private final EmployeeService employeeService;
    private final Clock clock;

    public SalaryController(SalaryService salaryService,
                            EmployeeService employeeService,
                            Clock clock) {
        this.salaryService = salaryService;
        this.employeeService = employeeService;
        this.clock = clock;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<Salary>> getEmployeeSalariesHistory(@PathVariable Long employeeId) {
        if (!employeeService.existById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return new ResponseEntity<>(salaryService.getSalariesByEmployeeId(employeeId), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<Salary>> getSalariesByMonth(
            @RequestParam
            @Pattern(regexp = "\\d{4}-\\d{2}", message = "Month must be in format YYYY-MM")
            String month) {
        YearMonth monthToCalculate = YearMonth.parse(month);
        if (monthToCalculate.isAfter(YearMonth.now(clock))) {
            throw new IllegalArgumentException("Month must not be in the future");
        }
        return new ResponseEntity<>(salaryService.getSalariesByMonthKey(month), HttpStatus.OK);
    }

    @PostMapping("/calculate")
    public ResponseEntity<SalaryCalculationResponse> calculateMonthlySalaries(
            @RequestParam
            @Pattern(regexp = "\\d{4}-\\d{2}", message = "Month must be in format YYYY-MM")
            String month) {
        YearMonth monthToCalculate = YearMonth.parse(month);
        if (monthToCalculate.isAfter(YearMonth.now(clock))) {
            throw new IllegalArgumentException("Month must not be in the future");
        }
        return new ResponseEntity<>(salaryService.processSalaryCalculation(month), HttpStatus.CREATED);
    }
}
