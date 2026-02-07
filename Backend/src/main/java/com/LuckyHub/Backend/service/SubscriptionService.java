package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.SubscriptionResponse;
import com.LuckyHub.Backend.model.SubscriptionTypes;

import java.math.BigDecimal;

public interface SubscriptionService {
    SubscriptionTypes getPlanByAmount(BigDecimal subAmount);

    SubscriptionResponse getUserSubscription(User user);

    void upgradeSubscription(User user, Payment payment);
}
