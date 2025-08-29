// src/main/java/com/company/leaveapp/dto/RegisterRequest.java
package com.company.leaveapp.dto;
import com.company.leaveapp.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record RegisterRequest(@NotBlank String name, @Email String email, @Size(min=6) String password, Role role) {}