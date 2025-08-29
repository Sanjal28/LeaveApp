// src/main/java/com/company/leaveapp/dto/UpdateUserStatusRequest.java
package com.company.leaveapp.dto;

import com.company.leaveapp.models.UserStatus;
import jakarta.validation.constraints.NotNull;
public record UpdateUserStatusRequest(@NotNull UserStatus status) {}