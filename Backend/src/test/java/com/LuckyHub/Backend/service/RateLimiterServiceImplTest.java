package com.LuckyHub.Backend.service;

import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceImplTest {

    @Mock
    private ProxyManager<String> proxyManager;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private BucketProxy bucket;

    @Mock
    private RemoteBucketBuilder<String> bucketBuilder;

    @InjectMocks
    private RateLimiterServiceImpl rateLimiterService;

    private final String email = "test@luckyhub.com";
    private final String endpoint = "giveaway";

    // Limit NOT reached (User can proceed)
    @Test
    void tryConsume_ShouldReturnFalse_WhenLimitIsNotReached() {
        when(proxyManager.builder()).thenReturn(bucketBuilder);
        when(bucketBuilder.build(anyString(), any(Supplier.class))).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        boolean isLimited = rateLimiterService.tryConsume(endpoint, email, 5);

        assertFalse(isLimited);
        verify(bucket).tryConsume(1);
    }

    // Limit REACHED (User should be blocked)
    @Test
    void tryConsume_ShouldReturnTrue_WhenLimitIsExceeded() {
        when(proxyManager.builder()).thenReturn(bucketBuilder);
        when(bucketBuilder.build(anyString(), any(Supplier.class))).thenReturn(bucket);

        when(bucket.tryConsume(1)).thenReturn(false);

        boolean isLimited = rateLimiterService.tryConsume(endpoint, email, 5);

        assertTrue(isLimited);
    }

    // Clearing limits from Redis
    @Test
    void clearLimit_ShouldDeleteKeys_WhenKeysExist() {
        Long userId = 1L;
        String pattern = "rate:*:" + userId + ":*";
        Set<String> mockKeys = Set.of("rate:giveaway:1:20260212");

        when(redisTemplate.keys(pattern)).thenReturn(mockKeys);

        rateLimiterService.clearLimit(userId);

        verify(redisTemplate).keys(pattern);
        verify(redisTemplate).delete(mockKeys);
    }

    // Clear limit failure
    @Test
    void clearLimit_ShouldHandleExceptionGracefully() {
        Long userId = 1L;
        when(redisTemplate.keys(anyString())).thenThrow(new RuntimeException("Redis down"));

        assertDoesNotThrow(() -> rateLimiterService.clearLimit(userId));
    }
}