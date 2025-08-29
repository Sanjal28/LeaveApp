// src/main/java/com/company/leaveapp/dto/UserResponse.java
package com.company.leaveapp.dto;

import com.company.leaveapp.models.Role;
import com.company.leaveapp.models.User;
import com.company.leaveapp.models.UserStatus;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        UserStatus status,
        String managerName
) {
    public static UserResponse fromEntity(User user) {
        String manager = user.getManager() != null ? user.getManager().getName() : null;
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getUserStatus(), manager);
    }
}