package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.model.PaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Payment findByPaymentId(String paymentId);
    Optional<Payment> findByUserId(Long userId);
    @Transactional
    @Modifying
    void deleteByUserIdAndStatusAndIdNot(Long userId, PaymentStatus status, Long id);

    @Transactional
    @Modifying
    void deleteByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime dateTime);

    Optional<Payment> findFirstByUserIdAndStatusOrderByCreatedAtDesc(Long userId, PaymentStatus status);
    List<Payment> findAllByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime dateTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM Payment p WHERE p.userId = :userId")
    void deleteByUserId(long userId);
}



