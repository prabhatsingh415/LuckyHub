package com.LuckyHub.Backend.event;

import com.LuckyHub.Backend.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class PaymentRefundEvent extends ApplicationEvent {
    private final User user;
    private final String planName;
    private final String orderId;
    private final BigDecimal amount;

    public PaymentRefundEvent(User user, String planName, String orderId, BigDecimal amount) {
        super(user);
        this.user = user;
        this.planName = planName;
        this.orderId = orderId;
        this.amount = amount;
    }
}
