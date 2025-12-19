package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.SubscriptionTypes;
import jakarta.servlet.http.HttpServletRequest;

public interface SubscriptionService {
    boolean verifyTheAmount(int subAmount);

    SubscriptionTypes getPlanByAmount(int subAmount);

    Long getUserId(HttpServletRequest request);
}
