package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.event.AccountDeleteEvent;
import com.LuckyHub.Backend.exception.InvalidOTPException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations; // Redis operations mock
    @Mock private ApplicationEventPublisher publisher;

    @InjectMocks
    private OtpServiceImpl otpService;

    private final String email = "user@luckyhub.com";
    private final String key = "delete_otp:user@luckyhub.com";

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // Verifies OTP generation, storage in Redis, and Event Publishing
    @Test
    void sendDeleteAccountOTP_ShouldStoreInRedisAndPublishEvent() {
        otpService.sendDeleteAccountOTP(email);

        verify(valueOperations).set(eq(key), anyString(), eq(10L), eq(TimeUnit.MINUTES));

        verify(publisher).publishEvent(isA(AccountDeleteEvent.class));
    }

    // OTP matches and then gets deleted from Redis
    @Test
    void verifyDeleteOTP_ShouldSucceed_WhenOtpMatches() {
        String correctOtp = "123456";
        when(valueOperations.get(key)).thenReturn(correctOtp);

        assertDoesNotThrow(() -> otpService.verifyDeleteOTP(email, correctOtp));

        verify(redisTemplate).delete(key);
    }

    // Throws exception when OTP is wrong
    @Test
    void verifyDeleteOTP_ShouldThrowException_WhenOtpIsWrong() {
        when(valueOperations.get(key)).thenReturn("123456");

        assertThrows(InvalidOTPException.class, () ->
                otpService.verifyDeleteOTP(email, "999999")
        );

        verify(redisTemplate, never()).delete(anyString());
    }

    // Throws exception when OTP is expired
    @Test
    void verifyDeleteOTP_ShouldThrowException_WhenOtpIsExpired() {
        when(valueOperations.get(key)).thenReturn(null);

        assertThrows(InvalidOTPException.class, () ->
                otpService.verifyDeleteOTP(email, "123456")
        );
    }
}