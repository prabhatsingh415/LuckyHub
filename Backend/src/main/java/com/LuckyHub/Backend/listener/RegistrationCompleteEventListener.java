package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.exception.UserEmailNotFoundException;
import com.LuckyHub.Backend.exception.EmailSendingFailedException;
import com.LuckyHub.Backend.service.EmailService;
import com.LuckyHub.Backend.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final EmailService emailService;

    public RegistrationCompleteEventListener(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        User user = event.getUser();
        if (user == null) {
            throw new UserEmailNotFoundException("User not found for registration email");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserEmailNotFoundException("User email is missing or empty");
        }

        // Generate token and save/update
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);

        String url = event.getUrl() + "?token=" + token;

        String body = "Hello " + user.getFirstName() + ",\n\n" +
                "Please click the link below to verify your account:\n" +
                url + "\n\n" +
                "Thanks,\nLuckyHub Team";

        try {
            emailService.sendEmail(user.getEmail(), "LuckyHub | Verify Your Email Address", body);
        } catch (Exception e) {
            throw new EmailSendingFailedException("Failed to send registration email to " + user.getEmail(), e);
        }
    }
}
