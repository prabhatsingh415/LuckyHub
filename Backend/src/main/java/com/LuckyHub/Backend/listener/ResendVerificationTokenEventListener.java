package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.ResendVerificationTokenEvent;
import com.LuckyHub.Backend.exception.EmailSendingFailedException;
import com.LuckyHub.Backend.exception.UserEmailNotFoundException;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import com.LuckyHub.Backend.service.EmailService;
import com.LuckyHub.Backend.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResendVerificationTokenEventListener implements ApplicationListener<ResendVerificationTokenEvent> {
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    public ResendVerificationTokenEventListener(UserService userService, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public void onApplicationEvent(ResendVerificationTokenEvent event) {
        User user = event.getUser();
        if (user == null) {
            throw new UserEmailNotFoundException("User not found for token resend");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserEmailNotFoundException("User email is missing or empty");
        }

        // generate token and update
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);

        String url = event.getUrl() + "verifyRegistration?token=" + token;

        String body = "Hello " + user.getFirstName() + ",\n\n" +
                "We noticed you requested a new verification link.\n" +
                "Please click the link below to verify your account:\n\n" +
                url + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Thanks,\nLuckyHub Team";

        try {
            emailService.sendEmail(user.getEmail(), "LuckyHub | Resend Verification Link", body);
        } catch (Exception e) {
            throw new EmailSendingFailedException("Failed to send verification email to " + user.getEmail(), e);
        }
    }
}
