package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.LastPaymentModel;
import com.LuckyHub.Backend.model.PaymentVerificationRequest;
import com.LuckyHub.Backend.model.RazorpayOrderResponse;
import com.LuckyHub.Backend.model.SubscriptionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentService {
    void createPartialPayment(Long userId, SubscriptionTypes planType, BigDecimal amount, String currency, String orderId, String receiptId);
    void completePayment(String orderId, String paymentId, boolean signatureVerified, LocalDateTime paymentDate, User user, String planByAmount, BigDecimal amount, String paymentMethod);

    LastPaymentModel getLastPayment(User user);

    void markPaymentFailed(String orderId);

    boolean processPaymentForCompletion(PaymentVerificationRequest request, User user);

    RazorpayOrderResponse initializePayment(User user, String planName);

    void processRazorpayWebhook(String payload, String signature);

    boolean checkIsPaymentSuccess(String orderId);

    void deletePayment(long userId);

    boolean processAndVerify(User user, PaymentVerificationRequest request);
}


