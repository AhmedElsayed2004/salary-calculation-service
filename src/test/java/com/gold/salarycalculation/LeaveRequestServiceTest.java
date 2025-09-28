package com.gold.salarycalculation;

import com.gold.salarycalculation.entity.Employee;
import com.gold.salarycalculation.entity.LeaveRequest;
import com.gold.salarycalculation.enums.LeaveRequestStatus;
import com.gold.salarycalculation.enums.LeaveType;
import com.gold.salarycalculation.repository.LeaveRequestRepository;
import com.gold.salarycalculation.service.LeaveRequestService;
import com.gold.salarycalculation.service.LeaveRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeaveRequestServiceTest {
    @Mock
    private LeaveRequestRepository leaveRequestRepository;
    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    @Test
    public void countApprovedUnpaidLeaveDays_ShouldReturnZero_WhenThereIsNoLeaveRequests() {
        //Arrange data
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setJoinDate(LocalDate.of(2020, 1, 1));
        String monthKey = "2022-01";
        LocalDate calculationDate = LocalDate.of(2022, 1, 15);

        when(leaveRequestRepository.findAllByEmployee_IdAndStatusAndLeaveType(
                1L,
                LeaveRequestStatus.APPROVED,
                LeaveType.UNPAID)).thenReturn(new ArrayList<>());

        //Act
        int result = leaveRequestService.countApprovedUnpaidLeaveDays(employee, monthKey, calculationDate);

        // Assert
        assertThat(result).isEqualTo(0);

    }

    @Test
    public void countApprovedUnpaidLeaveDays_ShouldReturnZero_WhenThereIsNoLeaveDays() {
        //Arrange data
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setJoinDate(LocalDate.of(2020, 1, 1));

        String monthKey = "2022-01";

        LocalDate calculationDate = LocalDate.of(2022, 1, 15);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStartDate(LocalDate.of(2020, 1, 1));
        leaveRequest.setEndDate(LocalDate.of(2020, 1, 10));


        when(leaveRequestRepository.findAllByEmployee_IdAndStatusAndLeaveType(
                1L,
                LeaveRequestStatus.APPROVED,
                LeaveType.UNPAID)).thenReturn(List.of(leaveRequest));

        //Act
        int result = leaveRequestService.countApprovedUnpaidLeaveDays(employee, monthKey, calculationDate);

        // Assert
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void countApprovedUnpaidLeaveDays_ShouldReturnNumber_WhenThereIsLeaveDays() {
        //Arrange data
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setJoinDate(LocalDate.of(2020, 1, 1));

        String monthKey = "2022-01";

        LocalDate calculationDate = LocalDate.of(2022, 1, 15);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStartDate(LocalDate.of(2020, 12, 1));
        leaveRequest.setEndDate(LocalDate.of(2022, 1, 10));


        when(leaveRequestRepository.findAllByEmployee_IdAndStatusAndLeaveType(
                1L,
                LeaveRequestStatus.APPROVED,
                LeaveType.UNPAID)).thenReturn(List.of(leaveRequest));

        //Act
        int result = leaveRequestService.countApprovedUnpaidLeaveDays(employee, monthKey, calculationDate);

        // Assert
        assertThat(result).isEqualTo(10);
    }
}
