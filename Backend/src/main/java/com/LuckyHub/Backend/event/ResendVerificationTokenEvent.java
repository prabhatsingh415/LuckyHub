package com.LuckyHub.Backend.event;

import com.LuckyHub.Backend.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ResendVerificationTokenEvent extends ApplicationEvent {
    private User user;
    private String url;
    public ResendVerificationTokenEvent(User user, String url) {
        super(user);
        this.user = user;
        this.url = url;
    }
}
