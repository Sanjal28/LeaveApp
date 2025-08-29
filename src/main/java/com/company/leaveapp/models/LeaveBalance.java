// src/main/java/com/company/leaveapp/models/LeaveBalance.java
package com.company.leaveapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "leave_balances", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "balance_year"}) // <-- UPDATE HERE
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- THIS IS THE FIX ---
    // Rename the database column to avoid the SQL keyword conflict.
    @Column(name = "balance_year", nullable = false)
    private int year;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal opening = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal credited = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal used = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal remaining = BigDecimal.ZERO;

    public void recalculateRemaining() {
        this.remaining = this.opening.add(this.credited).subtract(this.used);
    }
}