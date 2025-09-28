package com.gold.salarycalculation.service.util;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class CalculationDateResolver {
    private final Clock clock;

    public CalculationDateResolver(Clock clock) {
        this.clock = clock;
    }

    public LocalDate resolve(String monthKey) {
        LocalDate today = LocalDate.now(clock);
        YearMonth yearMonth = YearMonth.parse(monthKey);
        return today.getMonth().equals(yearMonth.getMonth())
                ? today
                : yearMonth.atEndOfMonth();
    }
}
