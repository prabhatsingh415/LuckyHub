package com.LuckyHub.Backend.service;


public interface RateLimiterService {
    boolean tryConsume(String endpoint, Long userId, int maxAttemptsPerDay);

    void clearLimit(Long id);
}
