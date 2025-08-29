// src/main/java/com/company/leaveapp/service/AdminService.java
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // <-- IMPORT THIS
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(AdminCreateUserRequest request) {
        User manager = null;
        if (request.managerId() != null) {
            manager = userRepository.findById(request.managerId())
                    .orElseThrow(() -> new EntityNotFoundException("Manager not found"));
            if (manager.getRole() != Role.MANAGER) {
                throw new IllegalArgumentException("Assigned manager must have the MANAGER role.");
            }
        }

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .manager(manager)
                .userStatus(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.save(user);

        // Convert double from request to BigDecimal for the entity
        LeaveBalance balance = LeaveBalance.builder()
                .user(savedUser)
                .year(LocalDate.now().getYear())
                .opening(BigDecimal.valueOf(request.initialLeaveBalance()))
                .build();
        balance.recalculateRemaining();
        leaveBalanceRepository.save(balance);

        return UserResponse.fromEntity(savedUser);
    }

    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());

        return new PageResponse<>(
                userResponses,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Transactional
    public UserResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setUserStatus(request.status());
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    public PageResponse<LeaveResponseDTO> getAllLeaves(Pageable pageable) {
        Page<LeaveRequest> leavePage = leaveRequestRepository.findAll(pageable);
        List<LeaveResponseDTO> leaveDTOs = leavePage.getContent().stream()
                .map(LeaveResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageResponse<>(
                leaveDTOs,
                leavePage.getNumber(),
                leavePage.getSize(),
                leavePage.getTotalElements(),
                leavePage.getTotalPages(),
                leavePage.isLast()
        );
    }
}