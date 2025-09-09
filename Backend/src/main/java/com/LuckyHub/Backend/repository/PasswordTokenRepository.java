package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {
    PasswordToken findByToken(String token);
    @Transactional
    void deleteByToken(String token);
}
