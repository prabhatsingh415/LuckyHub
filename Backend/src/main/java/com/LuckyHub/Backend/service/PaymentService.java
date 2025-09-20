package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentService {
    Payment createPartialPayment(Long userId, String planType, BigDecimal amount, String currency, String orderId, String receiptId);
    Payment completePayment(String orderId, String paymentId, boolean signatureVerified, LocalDateTime paymentDate);
    Payment getPaymentByOrderId(String orderId);
}
