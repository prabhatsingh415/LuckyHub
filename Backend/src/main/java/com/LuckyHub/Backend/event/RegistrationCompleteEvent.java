package com.LuckyHub.Backend.event;

import com.LuckyHub.Backend.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private final User user;
    private final String url;
    private final String token;
    public RegistrationCompleteEvent(User user, String url, String token) {
        super(user);
        this.user = user;
        this.url = url;
        this.token = token;
    }
}
