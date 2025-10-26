package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.ForgotPasswordEvent;
import com.LuckyHub.Backend.exception.EmailSendingFailedException;
import com.LuckyHub.Backend.exception.UserEmailNotFoundException;
import com.LuckyHub.Backend.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Async
public class ForgotPasswordEventListener implements ApplicationListener<ForgotPasswordEvent> {

    private final EmailService emailService;

    @Override
    public void onApplicationEvent(ForgotPasswordEvent event) {
        User user = event.getUser();
        if (user == null) {
            throw new UserEmailNotFoundException("User not found for registration email");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserEmailNotFoundException("User email is missing or empty");
        }

        String body = "Hello " + user.getFirstName() + ",\n\n" +
                "We received a request to reset your password. " +
                "Please click the link below to set a new password:\n\n" +
                event.getUrl() + "\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Thanks,\n" +
                "The LuckyHub Team";

        try {
            emailService.sendEmail(user.getEmail(), "LuckyHub | Reset Password", body);
        } catch (Exception e) {
            throw new EmailSendingFailedException("Failed to send password reset email to " + user.getEmail(), e);
        }
    }
}