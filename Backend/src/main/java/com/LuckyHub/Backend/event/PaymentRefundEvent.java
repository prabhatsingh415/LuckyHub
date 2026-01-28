package com.LuckyHub.Backend.event;

import com.LuckyHub.Backend.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentRefundEvent extends ApplicationEvent {
    private final User user;
    private final String planName;
    private final String orderId;
    private final int amount;

    public PaymentRefundEvent(User user, String planName, String orderId, int amount) {
        super(user);
        this.user = user;
        this.planName = planName;
        this.orderId = orderId;
        this.amount = amount;
    }
}
