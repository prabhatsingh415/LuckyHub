package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Payment findByPaymentId(String paymentId);
    Optional<Payment> findByUserId(Long userId);
}
