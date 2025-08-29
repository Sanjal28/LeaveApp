// src/main/java/com/company/leaveapp/dto/ResetBalanceRequest.java
package com.company.leaveapp.dto;

import jakarta.validation.constraints.Min;
public record ResetBalanceRequest(@Min(0) double openingBalance) {}