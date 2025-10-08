package com.gold.salarycalculation.scheduler;

import com.gold.salarycalculation.service.SalaryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
public class SalaryCalculationScheduler {

    private final SalaryService salaryService;

    public SalaryCalculationScheduler(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @Scheduled(cron = "0 59 23 L * *")
    public void processMonthlySalaryCalculation() {
        YearMonth yearMonth = YearMonth.now();
        String monthKey = yearMonth.toString();
        salaryService.processSalaryCalculation(monthKey);
    }
}
