package com.gold.salarycalculation.service;

import com.gold.salarycalculation.entity.Employee;

import java.time.LocalDate;

public interface LeaveRequestService {
    int countApprovedUnpaidLeaveDays(Employee employee, String monthKey, LocalDate calculationDate);
}
