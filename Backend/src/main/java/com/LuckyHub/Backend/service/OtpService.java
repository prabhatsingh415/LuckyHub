package com.LuckyHub.Backend.service;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {
    void sendDeleteAccountOTP(String email);

    void verifyDeleteOTP(String email, String otp);
}
