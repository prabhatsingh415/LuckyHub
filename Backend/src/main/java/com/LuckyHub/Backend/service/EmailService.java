package com.LuckyHub.Backend.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
