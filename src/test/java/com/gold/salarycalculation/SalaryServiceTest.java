package com.gold.salarycalculation;


import com.gold.salarycalculation.dto.SalaryCalculationResponse;
import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.entity.Salary;
import com.gold.salarycalculation.enums.EmployeeStatus;
import com.gold.salarycalculation.repository.EmployeeRepository;
import com.gold.salarycalculation.repository.SalaryRepository;
import com.gold.salarycalculation.service.SalaryCalculator;
import com.gold.salarycalculation.service.SalaryServiceImpl;
import com.gold.salarycalculation.service.factory.SalaryFactory;
import com.gold.salarycalculation.service.util.CalculationDateResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalaryServiceTest {
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private SalaryCalculator salaryCalculator;
    @Mock
    private SalaryFactory salaryFactory;
    @Mock
    private CalculationDateResolver calculationDateResolver;


    @InjectMocks
    private SalaryServiceImpl salaryService;

    @Test
    public void processSalaryCalculation_ShouldReturnZeroPayroll_WhenNoActiveEmployees() {
        // Arrange
        String monthKey = "2022-01";

        when(calculationDateResolver.resolve(monthKey)).thenReturn(LocalDate.of(2022, 1, 31));
        when(employeeRepository.findAllByStatusAndJoinDateLessThanEqual(eq(EmployeeStatus.ACTIVE)
                , any(LocalDate.class))).thenReturn(new ArrayList<>());

        //Act
        SalaryCalculationResponse result = salaryService.processSalaryCalculation(monthKey);

        // Assert
        assertThat(result.getTotalPayroll()).isEqualByComparingTo(BigDecimal.ZERO);


    }

    @Test
    public void processSalaryCalculation_ShouldReturnPayrollAmount_WhenEmployeesArePresent() {
        // Arrange
        String monthKey = "2022-01";
        Employee employee1 = new Employee();
        employee1.setId(1L);
        Employee employee2 = new Employee();
        employee2.setId(2L);

        LocalDate calculationDate = LocalDate.of(2022, 1, 31);

        Salary salary1 = new Salary();
        salary1.setNetSalary(BigDecimal.valueOf(10900));
        salary1.setEmployee(employee1);
        salary1.setMonthKey(monthKey);
        Salary salary2 = new Salary();
        salary2.setNetSalary(BigDecimal.valueOf(20950));
        salary2.setEmployee(employee2);
        salary2.setMonthKey(monthKey);



        when(calculationDateResolver.resolve(monthKey)).thenReturn(calculationDate);

        when(employeeRepository.findAllByStatusAndJoinDateLessThanEqual(eq(EmployeeStatus.ACTIVE)
                , any(LocalDate.class))).thenReturn(Arrays.asList(employee1, employee2));
        when(salaryRepository.existsByEmployee_IdAndCalculationDate(any(Long.class)
                , any(LocalDate.class))).thenReturn(false);

        when(salaryFactory.create(employee1, monthKey, calculationDate)).thenReturn(salary1);
        when(salaryFactory.create(employee2, monthKey, calculationDate)).thenReturn(salary2);

        when(salaryRepository.findByEmployee_IdAndMonthKey(any(Long.class), any(String.class)))
                .thenReturn(Optional.empty());
        when(salaryRepository.save(any(Salary.class))).thenAnswer(invocation -> invocation.getArgument(0));


        BigDecimal expectedPayroll = BigDecimal.valueOf(31850);

        //Act
        SalaryCalculationResponse result = salaryService.processSalaryCalculation(monthKey);

        //Assert
        assertThat(result.getTotalPayroll()).isEqualByComparingTo(expectedPayroll);
        assertThat(result.getEmployeesProcessed()).isEqualTo(2);
        assertThat(result.getMonth()).isEqualTo(monthKey);


    }
}
