package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.event.AccountDeleteEvent;
import com.LuckyHub.Backend.model.MailType;
import com.LuckyHub.Backend.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
@Slf4j
@AllArgsConstructor
public class AccountDeleteEventListener implements ApplicationListener<AccountDeleteEvent> {

    private final EmailService emailService;

    @Override
    public void onApplicationEvent(AccountDeleteEvent event) {
        String email = event.getEmail();
        String otpString = String.valueOf(event.getOtp());

        String subject = "LuckyHub | Action Required: Account Deletion Code 🚨";

        try {
            emailService.sendEmail(email, subject, otpString, MailType.ACCOUNT_DELETE);
        } catch (Exception e) {
            log.error("Failed to send account deletion email to {}", email, e);
        }
    }
}
