package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.model.SubscriptionTypes;

public interface SubscriptionService {
    boolean verifyTheAmount(int subAmount);

    SubscriptionTypes getPlanByAmount(int subAmount);

    Object getUserSubscription(String email);

    void upgradeSubscription(Payment payment);
}
