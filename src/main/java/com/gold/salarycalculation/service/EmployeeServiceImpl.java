package com.gold.salarycalculation.service;

import com.gold.salarycalculation.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public boolean existById(Long id) {
        return employeeRepository.existsById(id);
    }
}
