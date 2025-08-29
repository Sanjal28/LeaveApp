// src/main/java/com/company/leaveapp/controller/LeaveController.java
package com.company.leaveapp.controller;

import com.company.leaveapp.dto.*;
import com.company.leaveapp.models.User;
import com.company.leaveapp.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // == EMPLOYEE ENDPOINTS ==
    @PostMapping("/leaves")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveResponseDTO> applyForLeave(
            @Valid @RequestBody LeaveRequestDTO requestDTO,
            @AuthenticationPrincipal User employee) {
        LeaveResponseDTO response = leaveService.applyForLeave(requestDTO, employee.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaves/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<PageResponse<LeaveResponseDTO>> getMyLeaveRequests(
            @AuthenticationPrincipal User employee, Pageable pageable) {
        return ResponseEntity.ok(leaveService.getMyLeaveRequests(employee.getId(), pageable));
    }


    @GetMapping("/leaves/my/balance") // <-- NEW ENDPOINT
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveBalanceDTO> getMyCurrentBalance(@AuthenticationPrincipal User employee) {
        return ResponseEntity.ok(leaveService.getMyCurrentBalance(employee.getId()));
    }

    // == MANAGER ENDPOINTS ==
    @GetMapping("/manager/leaves")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PageResponse<LeaveResponseDTO>> getTeamLeaveRequests(
            @AuthenticationPrincipal User manager,
            Pageable pageable) { // <-- ADD Pageable
        return ResponseEntity.ok(leaveService.getPendingTeamLeaveRequests(manager.getId(), pageable));
    }

    @PutMapping("/manager/leaves/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LeaveResponseDTO> approveLeaveRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User manager) {
        return ResponseEntity.ok(leaveService.approveLeaveRequest(id, manager.getId()));
    }

    @PutMapping("/manager/leaves/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LeaveResponseDTO> rejectLeaveRequest(
            @PathVariable Long id,
            @Valid @RequestBody ManagerDecisionDTO decisionDTO,
            @AuthenticationPrincipal User manager) {
        return ResponseEntity.ok(leaveService.rejectLeaveRequest(id, manager.getId(), decisionDTO));
    }
}