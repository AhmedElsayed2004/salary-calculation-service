package com.gold.salarycalculation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryCalculationResponse {
    String month;
    int employeesProcessed;
    BigDecimal totalPayroll;

}
