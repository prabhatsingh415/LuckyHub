package com.LuckyHub.Backend.event;

import com.LuckyHub.Backend.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private final User user;
    private final String url;

    public RegistrationCompleteEvent(User user, String url) {
        super(user);
        this.user = user;
        this.url = url;
    }
}
