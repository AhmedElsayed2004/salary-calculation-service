package com.gold.salarycalculation.repository;

import com.gold.salarycalculation.entity.LeaveRequest;
import com.gold.salarycalculation.enums.LeaveRequestStatus;
import com.gold.salarycalculation.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findAllByEmployee_Id(Long employeeId);

    List<LeaveRequest> findAllByEmployee_IdAndStatusAndLeaveType(Long employeeId, LeaveRequestStatus status, LeaveType leaveType);
}
