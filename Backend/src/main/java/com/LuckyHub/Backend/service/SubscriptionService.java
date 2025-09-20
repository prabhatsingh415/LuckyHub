package com.LuckyHub.Backend.service;

public interface SubscriptionService {
    boolean verifyTheAmount(int subAmount);

    String getPlanByAmount(int subAmount);
}
