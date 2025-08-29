// src/main/java/com/company/leaveapp/repository/LeaveRequestRepository.java
package com.company.leaveapp.repository;

import com.company.leaveapp.models.LeaveRequest;
import com.company.leaveapp.models.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<LeaveRequest> findByManagerIdAndStatus(Long managerId, LeaveStatus status, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
            "AND lr.status IN :statuses " +
            "AND ((lr.startDate <= :endDate) AND (lr.endDate >= :startDate))")
    List<LeaveRequest> findOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate, List<LeaveStatus> statuses);
}