// src/main/java/com/company/leaveapp/dto/AdminCreateUserRequest.java
package com.company.leaveapp.dto;

import com.company.leaveapp.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminCreateUserRequest(
        @NotBlank String name,
        @Email String email,
        @Size(min=6) String password,
        @NotNull Role role,
        Long managerId, // Can be null if creating a manager
        double initialLeaveBalance
) {}