package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.LastPaymentModel;
import com.LuckyHub.Backend.model.SubscriptionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface PaymentService {
    void createPartialPayment(Long userId, SubscriptionTypes planType, BigDecimal amount, String currency, String orderId, String receiptId);
    void completePayment(String orderId, String paymentId, boolean signatureVerified, LocalDateTime paymentDate, User user, String planByAmount, int amount, String paymentMethod);
    Payment getPaymentByOrderId(String orderId);
    Payment getPaymentDataToUpgradeService(String paymentId);

    LastPaymentModel getLastPayment(String email);

    void markPaymentFailed(String orderId);

    boolean processPaymentForCompletion(Map<String, Object> data, User user);

    Map<String, Object> initializePayment(Long userId, String planName);

    void processRazorpayWebhook(String payload, String signature);

    boolean checkIsPaymentSuccess(String orderId);
}


