package com.LuckyHub.Backend.service;


public interface RateLimiterService {
    boolean tryConsume(String endpoint, String email, int maxAttemptsPerDay);
    void clearLimit(Long id);
}
