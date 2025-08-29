// src/main/java/com/company/leaveapp/repository/LeaveAdjustmentRepository.java
package com.company.leaveapp.repository;

import com.company.leaveapp.models.LeaveAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveAdjustmentRepository extends JpaRepository<LeaveAdjustment, Long> {
}