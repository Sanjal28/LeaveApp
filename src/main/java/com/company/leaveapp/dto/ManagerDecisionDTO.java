// src/main/java/com/company/leaveapp/dto/ManagerDecisionDTO.java
package com.company.leaveapp.dto;
import jakarta.validation.constraints.NotBlank;
public record ManagerDecisionDTO(@NotBlank String reason) {}