// src/main/java/com/company/leaveapp/dto/AuthRequest.java
package com.company.leaveapp.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record AuthRequest(@Email String email, @NotBlank String password) {}