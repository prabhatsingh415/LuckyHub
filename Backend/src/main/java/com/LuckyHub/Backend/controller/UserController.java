package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    public UserController(UserService userService, ApplicationEventPublisher publisher) {
        this.userService = userService;
        this.publisher = publisher;
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody  UserModel userModel,
                       final HttpServletRequest request){
       User user  =  userService.save(userModel);
       publisher.publishEvent(new RegistrationCompleteEvent(
                user,
               createURL(request)
       ));
       return user;
    }

    //For verifying user through E-Mail
    @GetMapping("/verifyRegistration")
    public String verifyUser(@RequestParam("token") String token){
      if(userService.verify(token).equalsIgnoreCase("Valid")) return "Success !";
      return "Invalid Token !";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserModel userModel) {

        String token = userService.verifyLogin(userModel);

        // Return JSON response
        return Map.of(
                "status", "success",
                "token", token
        );
    }

    @GetMapping("/resendToken")
    public ResponseEntity<?> resendVerificationToken(@RequestParam("token") String oldToken,
                                                     final HttpServletRequest request){
        if(oldToken == null || oldToken.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Token must not be empty"));
        }

        return userService.resendVerifyToken(oldToken, request, createURL(request));
    }

    private String createURL(HttpServletRequest request) {
        return "http://"+
                request.getServerName() +
                ":" +
                request.getServerPort() +
                "/" +
                request.getContextPath();
    }

}



