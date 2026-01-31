package com.LuckyHub.Backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AccountDeleteEvent extends ApplicationEvent {
    private final String email;
    private final int otp;

    public AccountDeleteEvent(String email, int otp) {
        super(email);
        this.email = email;
        this.otp = otp;
    }
}
