package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.model.SubscriptionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface PaymentService {
    Payment createPartialPayment(Long userId, SubscriptionTypes planType, BigDecimal amount, String currency, String orderId, String receiptId);
    Payment completePayment(String orderId, String paymentId, boolean signatureVerified, LocalDateTime paymentDate);
    Payment getPaymentByOrderId(String orderId);
    Payment getPaymentDataToUpgradeService(String paymentId);
}
