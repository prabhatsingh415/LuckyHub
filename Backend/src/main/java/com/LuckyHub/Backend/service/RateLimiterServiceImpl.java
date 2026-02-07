package com.LuckyHub.Backend.service;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Supplier;

@AllArgsConstructor
@Service
@Slf4j
public class RateLimiterServiceImpl implements RateLimiterService {
    private final ProxyManager<String> proxyManager;
    private final StringRedisTemplate redisTemplate;

    public boolean tryConsume(String endpoint, String email, int maxAttemptsPerDay) {

        // Unique daily key → resets every day automatically
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = "rate:" + endpoint + ":" + email + ":" + date;

        // Bucket config
        Supplier<BucketConfiguration> configSupplier = () -> {
            Bandwidth dailyLimit = Bandwidth.classic(
                    maxAttemptsPerDay,
                    Refill.intervally(maxAttemptsPerDay, Duration.ofDays(1))
            );
            return BucketConfiguration.builder()
                    .addLimit(dailyLimit)
                    .build();
        };

        // Load or create bucket from Redis
        Bucket bucket = proxyManager.builder().build(key, configSupplier);

        // Consume 1 attempt
        return !bucket.tryConsume(1);
    }

        @Override
        public void clearLimit(Long userId) {
            String pattern = "rate:*:" + userId + ":*";

            try {
                Set<String> keys = redisTemplate.keys(pattern);

                if (!keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    log.info("Cleared {} rate-limit keys for user ID: {}", keys.size(), userId);
                }
            } catch (Exception e) {
                log.error("Failed to clear rate limits for user {}: {}", userId, e.getMessage());
            }
        }
}

