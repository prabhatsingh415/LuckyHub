package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.MailType;

public interface EmailService {
    void sendEmail(String to, String subject, String body, MailType type);
    void sendAsyncEmail(String to, String subject, String  body, MailType type);
}
