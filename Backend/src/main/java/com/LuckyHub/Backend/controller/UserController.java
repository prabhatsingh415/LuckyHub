package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    public UserController(UserService userService, ApplicationEventPublisher publisher) {
        this.userService = userService;
        this.publisher = publisher;
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody  UserModel userModel, final HttpServletRequest request){
       User user  =  userService.save(userModel);
       publisher.publishEvent(new RegistrationCompleteEvent(
                user,
               createURL(request)
       ));
       return user;
    }

    private String createURL(HttpServletRequest request) {
        return "https://"+
                request.getServerName() +
                ":" +
                request.getServerPort() +
                "/" +
                request.getContextPath();
    }
}
