// src/main/java/com/company/leaveapp/dto/AuthResponse.java
package com.company.leaveapp.dto;
import com.company.leaveapp.models.Role;
import com.company.leaveapp.models.UserStatus;
public record AuthResponse(String token, String name, Role role, UserStatus status) {}