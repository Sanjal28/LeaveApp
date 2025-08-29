// src/main/java/com/company/leaveapp/dto/LeaveBalanceDTO.java
package com.company.leaveapp.dto;
public record LeaveBalanceDTO(int year, double opening, double credited, double used, double remaining) {}
