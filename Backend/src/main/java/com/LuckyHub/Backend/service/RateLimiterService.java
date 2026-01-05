package com.LuckyHub.Backend.service;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

@AllArgsConstructor
@Service
public class RateLimiterService {
    private final ProxyManager<String> proxyManager;

    public boolean tryConsume(String endpoint, Long userId, int maxAttemptsPerDay) {

        // Unique daily key → resets every day automatically
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = "rate:" + endpoint + ":" + userId + ":" + date;

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
        return bucket.tryConsume(1);
    }

}
