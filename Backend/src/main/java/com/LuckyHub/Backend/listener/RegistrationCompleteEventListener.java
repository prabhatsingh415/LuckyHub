package com.LuckyHub.Backend.listener;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
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
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);


        String url = event.getUrl()
                + "verifyRegistration?token="
                + token;

        String body = "Hello " + user.getFirstName() + ",\n\n" +
                "Please click the link below to verify your account:\n" +
                url + "\n\n" +
                "Thanks,\nLuckyHub Team";

        emailService.sendEmail(user.getEmail(), "For Account Verification", body);
    }
}
