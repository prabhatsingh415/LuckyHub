package com.LuckyHub.Backend.service;


import com.LuckyHub.Backend.event.AccountDeleteEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@Service
@AllArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService{

    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher publisher;

    private static final String OTP_PREFIX = "delete_otp:";

    @Override
    public void sendDeleteAccountOTP(String email) {
        int otp = 100000 + RandomGenerator.getDefault().nextInt(900000);
        String key = OTP_PREFIX + email;

        redisTemplate.opsForValue().set(key, String.valueOf(otp), 10, TimeUnit.MINUTES);
        log.info("OTP stored in Redis for email: {}", email);

        publisher.publishEvent(new AccountDeleteEvent(email, otp));
    }

    @Override
    public boolean verifyDeleteOTP(String email, String otp) {
        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
