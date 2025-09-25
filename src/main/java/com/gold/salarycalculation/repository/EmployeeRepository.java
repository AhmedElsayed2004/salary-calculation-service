package com.gold.salarycalculation.repository;

import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByStatus(EmployeeStatus status);
}
