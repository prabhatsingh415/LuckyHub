package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.PaymentRefundEvent;
import com.LuckyHub.Backend.exception.EmailSendingFailedException;
import com.LuckyHub.Backend.exception.UserEmailNotFoundException;
import com.LuckyHub.Backend.service.EmailService;
import com.LuckyHub.Backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
@Slf4j
@AllArgsConstructor
public class PaymentRefundEventListener implements ApplicationListener<PaymentRefundEvent> {
    private final UserService userService;
    private final EmailService emailService;

    @Override
    public void onApplicationEvent(PaymentRefundEvent event) {
        User user = event.getUser();
        if (user == null) {
            throw new UserEmailNotFoundException("User not found for token resend");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserEmailNotFoundException("User email is missing or empty");
        }

        String subject = "LuckyHub | Important Update: Order #%s ⚠️".formatted(event.getOrderId());

        String msgBody = """
        Hi %s %s,
        
        We noticed a technical glitch while processing your payment for the %s. 
        
        What happened?
        Even if your bank has deducted or "held" the amount, the transaction was NOT completed on our end. 
        Because of this, your subscription is currently not active.
        
        Don't worry about your money:
        Since we haven't "captured" the payment, your bank will automatically refund the amount 
        to your account within 5-7 business days. You don't need to raise any support tickets; 
        the banking system handles this return process automatically.
        
        Order Details:
        --------------------------
        Order ID:   %s
        Amount:     ₹%s
        Status:     Failed/Pending Refund
        --------------------------
        
        What next?
        You can try the payment again from your Dashboard. We suggest checking your 
        internet connection or trying a different payment method.
        
        Sorry for the inconvenience!
        
        Best Regards,
        Team LuckyHub
        """.formatted(
                    user.getFirstName(),
                    user.getLastName(),
                    event.getPlanName(),
                    event.getOrderId(),
                    event.getAmount()
            );

        try {
            emailService.sendEmail(user.getEmail(), subject, msgBody);
        } catch (Exception e) {
            throw new EmailSendingFailedException("Failed to send verification email to " + user.getEmail(), e);
        }
    }
}
