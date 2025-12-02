package com.gold.salarycalculation.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long employeeId) {
        super("Employee with id " + employeeId + " not found");
    }
}
