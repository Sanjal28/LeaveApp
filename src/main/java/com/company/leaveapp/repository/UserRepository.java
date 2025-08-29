// src/main/java/com/company/leaveapp/repository/UserRepository.java
package com.company.leaveapp.repository;

import com.company.leaveapp.models.User;
import com.company.leaveapp.models.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}