// src/main/java/com/company/leaveapp/repository/LeaveBalanceRepository.java
package com.company.leaveapp.repository;

import com.company.leaveapp.models.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUserIdAndYear(Long userId, int year);
}