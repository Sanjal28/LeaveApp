// src/main/java/com/company/leaveapp/service/LeaveService.java
package com.company.leaveapp.service;

import com.company.leaveapp.dto.*;
import com.company.leaveapp.models.*;
import com.company.leaveapp.repository.LeaveBalanceRepository;
import com.company.leaveapp.repository.LeaveRequestRepository;
import com.company.leaveapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public LeaveResponseDTO applyForLeave(LeaveRequestDTO requestDTO, Long employeeId) {
        User employee = userRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        long requestedDaysLong = ChronoUnit.DAYS.between(requestDTO.startDate(), requestDTO.endDate()) + 1;
        BigDecimal requestedDays = BigDecimal.valueOf(requestedDaysLong); // Convert to BigDecimal

        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingRequests(
                employeeId, requestDTO.startDate(), requestDTO.endDate(), List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED));
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Leave request overlaps with an existing request.");
        }

        int currentYear = requestDTO.startDate().getYear();
        LeaveBalance balance = leaveBalanceRepository.findByUserIdAndYear(employeeId, currentYear)
                .orElseThrow(() -> new IllegalStateException("No leave balance found for the year."));

        // Use compareTo for BigDecimal comparison
        if (balance.getRemaining().compareTo(requestedDays) < 0) {
            throw new IllegalStateException("Insufficient leave balance.");
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .manager(employee.getManager())
                .type(requestDTO.type())
                .startDate(requestDTO.startDate())
                .endDate(requestDTO.endDate())
                .days(requestedDays)
                .reason(requestDTO.reason())
                .status(LeaveStatus.PENDING)
                .build();

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        emailService.sendLeaveAppliedNotification(leaveRequest);
        return LeaveResponseDTO.fromEntity(leaveRequest);
    }

    public PageResponse<LeaveResponseDTO> getMyLeaveRequests(Long employeeId, Pageable pageable) {
        Page<LeaveRequest> leavePage = leaveRequestRepository.findByEmployeeId(employeeId, pageable);
        List<LeaveResponseDTO> dtos = leavePage.getContent().stream().map(LeaveResponseDTO::fromEntity).collect(Collectors.toList());
        return new PageResponse<>(dtos, leavePage.getNumber(), leavePage.getSize(), leavePage.getTotalElements(), leavePage.getTotalPages(), leavePage.isLast());
    }

    public PageResponse<LeaveResponseDTO> getPendingTeamLeaveRequests(Long managerId, Pageable pageable) {
        Page<LeaveRequest> leavePage = leaveRequestRepository.findByManagerIdAndStatus(managerId, LeaveStatus.PENDING, pageable);
        List<LeaveResponseDTO> dtos = leavePage.getContent().stream().map(LeaveResponseDTO::fromEntity).collect(Collectors.toList());
        return new PageResponse<>(dtos, leavePage.getNumber(), leavePage.getSize(), leavePage.getTotalElements(), leavePage.getTotalPages(), leavePage.isLast());
    }

    @Transactional
    public LeaveResponseDTO approveLeaveRequest(Long leaveRequestId, Long managerId) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() -> new EntityNotFoundException("Leave request not found."));
        User manager = userRepository.findById(managerId).orElseThrow(() -> new EntityNotFoundException("Manager not found."));

        if (!lr.getManager().getId().equals(managerId)) {
            throw new AccessDeniedException("You are not authorized to approve this request.");
        }
        if (lr.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request is not in PENDING state.");
        }

        LeaveBalance balance = leaveBalanceRepository.findByUserIdAndYear(lr.getEmployee().getId(), lr.getStartDate().getYear())
                .orElseThrow(() -> new IllegalStateException("Leave balance not found."));

        // Use BigDecimal's add() method
        balance.setUsed(balance.getUsed().add(lr.getDays()));
        balance.recalculateRemaining();
        leaveBalanceRepository.save(balance);

        lr.setStatus(LeaveStatus.APPROVED);
        lr.setDecidedBy(manager);
        lr.setDecidedAt(LocalDateTime.now());
        leaveRequestRepository.save(lr);

        // Convert remaining balance to double for the email notification
        emailService.sendLeaveStatusNotification(lr, balance.getRemaining().doubleValue());
        return LeaveResponseDTO.fromEntity(lr);
    }

    @Transactional
    public LeaveResponseDTO rejectLeaveRequest(Long leaveRequestId, Long managerId, ManagerDecisionDTO decisionDTO) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() -> new EntityNotFoundException("Leave request not found."));
        User manager = userRepository.findById(managerId).orElseThrow(() -> new EntityNotFoundException("Manager not found."));

        if (!lr.getManager().getId().equals(managerId)) {
            throw new AccessDeniedException("You are not authorized to reject this request.");
        }
        if (lr.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request is not in PENDING state.");
        }

        lr.setStatus(LeaveStatus.REJECTED);
        lr.setDecisionReason(decisionDTO.reason());
        lr.setDecidedBy(manager);
        lr.setDecidedAt(LocalDateTime.now());
        leaveRequestRepository.save(lr);

        emailService.sendLeaveStatusNotification(lr, null);
        return LeaveResponseDTO.fromEntity(lr);
    }

    public LeaveBalanceDTO getMyCurrentBalance(Long employeeId) {
        int currentYear = LocalDate.now().getYear();
        LeaveBalance balance = leaveBalanceRepository.findByUserIdAndYear(employeeId, currentYear)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found for the current year"));

        // Convert BigDecimal fields to double for the DTO
        return new LeaveBalanceDTO(
                balance.getYear(),
                balance.getOpening().doubleValue(),
                balance.getCredited().doubleValue(),
                balance.getUsed().doubleValue(),
                balance.getRemaining().doubleValue()
        );
    }
}