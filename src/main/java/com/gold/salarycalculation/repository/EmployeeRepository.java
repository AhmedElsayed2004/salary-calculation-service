package com.gold.salarycalculation.repository;

import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByStatus(EmployeeStatus status);

    List<Employee> findAllByStatusAndJoinDateLessThanEqual(EmployeeStatus status, LocalDate joinDate);

    @Query("""
            SELECT DISTINCT e
            FROM Employee e
            LEFT JOIN FETCH e.leaveRequests lr
            WHERE e.status = 'ACTIVE'
            AND e.joinDate <= :calculationDate
            """)
    List<Employee> findActiveEmployeesEligibleForPayroll(
            @Param("monthKey") String monthKey,
            @Param("calculationDate") LocalDate calculationDate
    );


    @Query("""
            SELECT DISTINCT e
            FROM Employee e
            LEFT JOIN FETCH e.salaries s
            WHERE e.status = 'ACTIVE'
            AND e.joinDate <= :calculationDate
            AND s.monthKey = :monthKey
            """)
    List<Employee> findActiveEmployeesWithSalaryForMonth(
            @Param("monthKey") String monthKey,
            @Param("calculationDate") LocalDate calculationDate
    );


}
