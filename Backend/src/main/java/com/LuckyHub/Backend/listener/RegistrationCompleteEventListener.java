package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.exception.UserEmailNotFoundException;
import com.LuckyHub.Backend.exception.EmailSendingFailedException;
import com.LuckyHub.Backend.model.MailType;
import com.LuckyHub.Backend.service.EmailService;
import com.LuckyHub.Backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Async
@AllArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final EmailService emailService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();

        userService.saveVerificationTokenForUser(user, event.getToken());
        String url = event.getUrl() + "?token=" + event.getToken();

        try {
            emailService.sendEmail(user.getEmail(), "LuckyHub | Verify Your Email Address", url, MailType.VERIFICATION);
            emailService.sendEmail(user.getEmail(), "habibi", "Okk", MailType.PAYMENT_REFUND);
        } catch (Exception e) {
            throw new EmailSendingFailedException("Failed to send registration email", e);
        }
    }
}
