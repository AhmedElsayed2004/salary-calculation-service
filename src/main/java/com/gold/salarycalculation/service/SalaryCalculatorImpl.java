package com.gold.salarycalculation.service;

import com.gold.salarycalculation.entity.Employee;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Component
public class SalaryCalculatorImpl implements SalaryCalculator {

    LeaveRequestService leaveRequestService;

    public SalaryCalculatorImpl(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    public BigDecimal calculateBasicSalary(Employee employee, String monthKey, LocalDate calculationDate) {
        YearMonth yearMonth = YearMonth.parse(monthKey);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate employeeJoinDate = employee.getJoinDate();
        BigDecimal perDaySalary = calculatePerDaySalary(employee.getBasicSalary(), monthKey);
        long numberOfWorkedDays;
        if (employeeJoinDate.isAfter(firstDay)) {
            numberOfWorkedDays = ChronoUnit.DAYS.between(employeeJoinDate, calculationDate) + 1;
        } else {
            if(calculationDate.getDayOfMonth() == calculationDate.lengthOfMonth())
                return employee.getBasicSalary();
            numberOfWorkedDays = ChronoUnit.DAYS.between(firstDay, calculationDate) + 1;
        }
        return perDaySalary.multiply(BigDecimal.valueOf(numberOfWorkedDays));
    }

    public BigDecimal calculateAllowances(Employee employee, LocalDate calculationDate) {
        if (calculationDate.getDayOfMonth() == calculationDate.lengthOfMonth())
            return employee.getAllowances();
        else
            return BigDecimal.ZERO;
    }

    public BigDecimal calculateDeduction(Employee employee, String monthKey, LocalDate calculationDate) {
        BigDecimal perDaySalary = calculatePerDaySalary(employee.getBasicSalary(), monthKey);
        int numberOfApprovedUnpaidLeavedDays = leaveRequestService.countApprovedUnpaidLeaveDays(employee, monthKey, calculationDate);
        return perDaySalary.multiply(BigDecimal.valueOf(numberOfApprovedUnpaidLeavedDays));
    }

    public BigDecimal calculatePerDaySalary(BigDecimal basicSalary, String monthKey) {
        YearMonth yearMonth = YearMonth.parse(monthKey);
        return basicSalary.divide(BigDecimal.valueOf(yearMonth.lengthOfMonth()), 2, RoundingMode.HALF_UP);

    }

}
