package com.LuckyHub.Backend.event;

import com.LuckyHub.Backend.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ForgotPasswordEvent extends ApplicationEvent {
    private final User user;
    private final String url;

    public ForgotPasswordEvent(User user, String url) {
        super(user);
        this.user = user;
        this.url = url;
    }
}
