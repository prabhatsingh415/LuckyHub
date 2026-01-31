package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.event.AccountDeleteEvent;
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
        int otp = event.getOtp();
        String subject = "Important: Verify Your Account Deletion - LuckyHub";

        String body = String.format(
                "Hi,\n\n" +
                        "You've requested to delete your LuckyHub account. Use the code below to proceed:\n\n" +
                        "OTP: %s\n\n" +
                        "Note: This code will expire in 10 minutes. If this wasn't you, no further action is needed.\n\n" +
                        "Stay safe,\n" +
                        "LuckyHub Support",
                otp
        );

        emailService.sendEmail(email, subject, body);
    }
}
