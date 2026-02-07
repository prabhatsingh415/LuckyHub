package com.LuckyHub.Backend.event;

import com.LuckyHub.Backend.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class PaymentSuccessfulEvent extends ApplicationEvent {
    private final User user;
    private final String paymentId;
    private final String orderId;
    private final BigDecimal amount;
    private final String planName;

    public PaymentSuccessfulEvent(User user, String paymentId, String orderId, BigDecimal amount, String planName) {
        super(user);
        this.user = user;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.planName = planName;
    }
}
