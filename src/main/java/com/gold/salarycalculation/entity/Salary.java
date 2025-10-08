package com.gold.salarycalculation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Constraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "salaries",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id", "month_key"})})
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month_key", length = 7, nullable = false)
    private String monthKey;

    @Column(name = "calculation_date", nullable = false)
    private LocalDate calculationDate;

    @Column(name = "basic_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "allowances", nullable = false, precision = 15, scale = 2)
    private BigDecimal allowances;

    @Column(name = "deductions", nullable = false, precision = 15, scale = 2)
    private BigDecimal deductions;

    @Column(name = "net_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public BigDecimal calculateNetSalary() {
        return basicSalary.add(allowances).subtract(deductions);
    }

}
