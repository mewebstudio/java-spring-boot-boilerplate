package com.mewebstudio.javaspringbootboilerplate.event;

import com.mewebstudio.javaspringbootboilerplate.entity.User;
import org.springframework.context.ApplicationEvent;

public class UserEmailVerificationSendEvent extends ApplicationEvent {
    private final User user;

    public UserEmailVerificationSendEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
