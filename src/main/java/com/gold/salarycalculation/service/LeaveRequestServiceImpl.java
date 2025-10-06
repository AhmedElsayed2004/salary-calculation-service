package com.gold.salarycalculation.service;

import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.entity.LeaveRequest;
import com.gold.salarycalculation.enums.LeaveRequestStatus;
import com.gold.salarycalculation.enums.LeaveType;
import com.gold.salarycalculation.repository.LeaveRequestRepository;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    public int countApprovedUnpaidLeaveDays(Employee employee, String monthKey, LocalDate calculationDate) {
        YearMonth yearMonth = YearMonth.parse(monthKey);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate employeeJoinDate = employee.getJoinDate();
        LocalDate firstDayEmployeeWork;
        if (employeeJoinDate.isAfter(firstDay))
            firstDayEmployeeWork = employeeJoinDate;
        else
            firstDayEmployeeWork = firstDay;
        List<LeaveRequest> leaveRequests = employee.getLeaveRequests();
        return leaveRequests.stream()
                .filter(e ->
                        e.getLeaveType().equals(LeaveType.UNPAID) &&
                                e.getStatus().equals(LeaveRequestStatus.APPROVED)
                )
                .map(e -> countLeaveDays(
                        e.getStartDate(),
                        e.getEndDate(),
                        firstDayEmployeeWork,
                        calculationDate
                ))
                .reduce(0, Integer::sum);
    }

    private int countLeaveDays(LocalDate leaveStart,
                               LocalDate leaveEnd,
                               LocalDate workStart,
                               LocalDate workEnd) {
        Range<LocalDate> leaveDays = Range.of(leaveStart, leaveEnd);
        Range<LocalDate> workDays = Range.of(workStart, workEnd);

        if (!workDays.isOverlappedBy(leaveDays)) {
            return 0;
        }

        Range<LocalDate> leaveWithinWorkPeriod = workDays.intersectionWith(leaveDays);

        return (int) ChronoUnit.DAYS.between(leaveWithinWorkPeriod.getMinimum(), leaveWithinWorkPeriod.getMaximum()) + 1;
    }
}
