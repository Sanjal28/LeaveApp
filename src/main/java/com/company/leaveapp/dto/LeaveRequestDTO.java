// src/main/java/com/company/leaveapp/dto/LeaveRequestDTO.java
package com.company.leaveapp.dto;
import com.company.leaveapp.models.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record LeaveRequestDTO(
        @NotNull LeaveType type,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull @FutureOrPresent LocalDate endDate,
        @NotBlank String reason
) {}