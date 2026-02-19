package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.ResendVerificationTokenEvent;
import com.LuckyHub.Backend.exception.EmailSendingFailedException;
import com.LuckyHub.Backend.exception.UserEmailNotFoundException;
import com.LuckyHub.Backend.model.MailType;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
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
public class ResendVerificationTokenEventListener implements ApplicationListener<ResendVerificationTokenEvent> {
    private final UserService userService;
    private final EmailService emailService;

    @Override
    public void onApplicationEvent(ResendVerificationTokenEvent event) {
        User user = event.getUser();
        if (user == null) {
            throw new UserEmailNotFoundException("User not found for token resend");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserEmailNotFoundException("User email is missing or empty");
        }

        userService.saveVerificationTokenForUser(user, event.getToken());

        String url = event.getUrl() + "?token=" + event.getToken();

        try {
            emailService.sendEmail(user.getEmail(), "LuckyHub | New Verification Link", url, MailType.RESEND_VERIFICATION);
        } catch (Exception e) {
            throw new EmailSendingFailedException("Failed to send verification email", e);
        }
    }
}
