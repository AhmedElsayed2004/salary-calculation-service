package com.gold.salarycalculation;

import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.service.LeaveRequestService;
import com.gold.salarycalculation.service.SalaryCalculatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalaryCalculatorTest {
    @Mock
    private LeaveRequestService leaveRequestService;

    @InjectMocks
    private SalaryCalculatorImpl salaryCalculator;

    @Test
    public void calculateBasicSalary_ShouldProrate_WhenJoinDateNotStartOfMonth() {
        //Arrange
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setJoinDate(LocalDate.of(2022, 1, 16));
        employee.setBasicSalary(BigDecimal.valueOf(10000));

        String monthKey = "2022-01";

        LocalDate calculationDate = LocalDate.of(2022, 1, 30);

        BigDecimal expectedSalary = BigDecimal.valueOf(10000)
                .divide(BigDecimal.valueOf(31), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(15));

        // Act
        BigDecimal result = salaryCalculator.calculateBasicSalary(employee, monthKey, calculationDate);

        assertThat(result).isEqualTo(expectedSalary);
    }

    @Test
    public void calculateBasicSalary_ShouldReturnFullSalary_WhenJoinedBeforeMonthAndCalculatedOnLastDay() {
        //Arrange
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setJoinDate(LocalDate.of(2021, 12, 15));
        employee.setBasicSalary(BigDecimal.valueOf(10000));

        String monthKey = "2022-01";

        LocalDate calculationDate = LocalDate.of(2022, 1, 31);

        BigDecimal expectedSalary = BigDecimal.valueOf(10000);

        // Act
        BigDecimal result = salaryCalculator.calculateBasicSalary(employee, monthKey, calculationDate);

        assertThat(result).isEqualTo(expectedSalary);
    }

    @Test
    public void calculateAllowances_ShouldReturnZero_WhenCalculatedAtMidMonth() {
        // Arrange
        Employee employee = new Employee();
        employee.setAllowances(BigDecimal.valueOf(2000));
        LocalDate calculationDate = LocalDate.of(2022, 1, 22);

        // Act
        BigDecimal result = salaryCalculator.calculateAllowances(employee, calculationDate);

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void calculateAllowances_ShouldReturnFullAllowances_WhenCalculatedOnLastDay() {
        // Arrange
        Employee employee = new Employee();
        employee.setAllowances(BigDecimal.valueOf(2000));
        LocalDate calculationDate = LocalDate.of(2022, 1, 31);

        // Act
        BigDecimal result = salaryCalculator.calculateAllowances(employee, calculationDate);

        assertThat(result).isEqualTo(BigDecimal.valueOf(2000));
    }

    @Test
    public void calculateDeduction_ShouldReturnZero_WhenThereIsNoLeaveDays() {
        // Arrange
        Employee employee = new Employee();
        employee.setBasicSalary(BigDecimal.valueOf(10000));
        String monthKey = "2022-01";
        LocalDate calculationDate = LocalDate.of(2022, 1, 31);

        when(leaveRequestService.countApprovedUnpaidLeaveDays(employee, monthKey, calculationDate)).thenReturn(0);

        // Act
        BigDecimal result = salaryCalculator.calculateDeduction(employee, monthKey, calculationDate);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void calculateDeduction_ShouldReturnNumber_WhenThereIsLeaveDays() {
        // Arrange
        Employee employee = new Employee();
        employee.setBasicSalary(BigDecimal.valueOf(30000));
        String monthKey = "2022-04";
        LocalDate calculationDate = LocalDate.of(2022, 4, 30);

        BigDecimal expectedDeduction = BigDecimal.valueOf(3000);

        when(leaveRequestService.countApprovedUnpaidLeaveDays(employee, monthKey, calculationDate)).thenReturn(3);

        // Act
        BigDecimal result = salaryCalculator.calculateDeduction(employee, monthKey, calculationDate);

        assertThat(result).isEqualByComparingTo(expectedDeduction);
    }
}
