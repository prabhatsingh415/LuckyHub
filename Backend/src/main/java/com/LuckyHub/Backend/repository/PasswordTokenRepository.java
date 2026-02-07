package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.PasswordToken;
import com.LuckyHub.Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {
    Optional<PasswordToken> findByToken(String token);

    @Transactional
    void deleteByToken(String token);

    Optional<PasswordToken> findByUser(User user);
}

