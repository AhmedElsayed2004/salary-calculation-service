package com.gold.salarycalculation.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long rmployeeId) {
        super("Employee with id " + rmployeeId + " not found");
    }
}
