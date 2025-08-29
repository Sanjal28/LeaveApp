// src/main/java/com/company/leaveapp/dto/LeaveResponseDTO.java
package com.company.leaveapp.dto;

import com.company.leaveapp.models.LeaveRequest;
import com.company.leaveapp.models.LeaveStatus;
import com.company.leaveapp.models.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveResponseDTO(
        Long id,
        String employeeName,
        String managerName,
        LeaveType type,
        LocalDate startDate,
        LocalDate endDate,
        // This field remains a double for the API response
        Double days,
        String reason,
        LeaveStatus status,
        String decisionReason,
        LocalDateTime createdAt
) {
    public static LeaveResponseDTO fromEntity(LeaveRequest lr) {
        return new LeaveResponseDTO(
                lr.getId(),
                lr.getEmployee().getName(),
                lr.getManager() != null ? lr.getManager().getName() : null,
                lr.getType(),
                lr.getStartDate(),
                lr.getEndDate(),
                // Convert BigDecimal to Double here
                lr.getDays() != null ? lr.getDays().doubleValue() : null,
                lr.getReason(),
                lr.getStatus(),
                lr.getDecisionReason(),
                lr.getCreatedAt()
        );
    }
}