package com.gold.salarycalculation.service;

import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.entity.LeaveRequest;
import com.gold.salarycalculation.enums.LeaveRequestStatus;
import com.gold.salarycalculation.enums.LeaveType;
import com.gold.salarycalculation.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public int countApprovedUnpaidLeaveDays(Employee employee, String monthKey, LocalDate calculationDate) {
        YearMonth yearMonth = YearMonth.parse(monthKey);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate employeeJoinDate = employee.getJoinDate();
        LocalDate firstDayEmployeeWork;
        if (employeeJoinDate.isAfter(firstDay))
            firstDayEmployeeWork = employeeJoinDate;
        else
            firstDayEmployeeWork = firstDay;
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByEmployee_IdAndStatusAndLeaveType(
                employee.getId(),
                LeaveRequestStatus.APPROVED,
                LeaveType.UNPAID);
        return leaveRequests.stream()
                .map(e -> overlapDays(
                        e.getStartDate(),
                        e.getEndDate(),
                        firstDayEmployeeWork,
                        calculationDate
                ))
                .reduce(0, Integer::sum);
    }

    private int overlapDays(LocalDate range1Start,
                            LocalDate range1End,
                            LocalDate range2Start,
                            LocalDate range2End) {
        LocalDate start = range1Start.isAfter(range2Start) ? range1Start : range2Start;
        LocalDate end = range1End.isBefore(range2End) ? range1End : range2End;

        if (start.isAfter(end)) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }
}
