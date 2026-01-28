package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.PaymentSuccessfulEvent;
import com.LuckyHub.Backend.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class PaymentSuccessfulEventListener {

    private final EmailService emailService;

    @EventListener
    public void onPaymentSuccess(PaymentSuccessfulEvent event) {
        String orderId = event.getOrderId();
        User user = event.getUser();

        log.info("[PAYMENT-EVENT] Received success event for Order ID: {} | Payment ID: {}",
                orderId, event.getPaymentId());

        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("[PAYMENT-EVENT] Skipping email for Order ID: {}. Reason: User email is missing.", orderId);
            return;
        }

        String subject = "LuckyHub | Payment Successful! 🎉 Subscription Activated";
        String msgBody = generateEmailBody(user, event);

        try {
            log.info("[PAYMENT-EVENT] Attempting to send success email to: {} for Order: {}",
                    user.getEmail(), orderId);

            emailService.sendAsyncEmail(user.getEmail(), subject, msgBody);

            log.info("[PAYMENT-EVENT] SUCCESS: Email delivered to {} for Order: {}",
                    user.getEmail(), orderId);

        } catch (Exception e) {
            log.error("[PAYMENT-EVENT] FAILED: Could not send email for Order: {}. Error: {}",
                    orderId, e.getMessage(), e);
        }
    }

    private String generateEmailBody(User user, PaymentSuccessfulEvent event) {
        return """
            Hi %s %s,
            
            Great news! We have successfully received your payment for the %s. 
            Your subscription is now active.
            
            Order Details:
            --------------------------
            Order ID:   %s
            Payment ID: %s
            Plan:       %s
            Amount:     ₹%s
            --------------------------
            
            Team LuckyHub
            """.formatted(
                user.getFirstName(), user.getLastName(), event.getPlanName(),
                event.getOrderId(), event.getPaymentId(), event.getPlanName(), event.getAmount()
        );
    }
}