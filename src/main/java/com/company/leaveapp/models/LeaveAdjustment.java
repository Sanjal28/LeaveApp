// src/main/java/com/company/leaveapp/models/LeaveAdjustment.java
package com.company.leaveapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_adjustments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    // --- THIS IS THE FIX ---
    @Column(name = "adjustment_year", nullable = false)
    private int year;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal days;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}