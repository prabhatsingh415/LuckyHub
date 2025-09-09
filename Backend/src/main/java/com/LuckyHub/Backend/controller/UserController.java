package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.PasswordModel;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request){

        Optional<User> user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user.isPresent()){
            String token = UUID.randomUUID().toString();
            User user1 = user.get();
            userService.createResendPasswordToken(user1, token);
            url = userService.GeneratePasswordResetURL(createURL(request), token);
        }else {
            throw  new UserNotFoundException("No user found ! recheck your password");
        }

        return ResponseEntity.ok()
                .body(
                    Map.of(
                        "status", "success",
                        "url", url,
                        "message", "User is verified, now can be redirected for password reset"
                    )
                );
    }

    @PostMapping("/savePassword")
    public ResponseEntity<?> savePassword(@RequestParam("token") String token,
                                          @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordToken(token);

        if(!result.equalsIgnoreCase("Valid")){
          return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                  Map.of(
                          "status", "error",
                          "message", "Invalid or expired token"
                  )
          );
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);

        if(user.isPresent()){
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            userService.deletePasswordToken(token);
            return ResponseEntity.ok(
                    Map.of(
                            "status", "success",
                            "message", "Password has been reset successfully."
                    )
            );
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "status", "error",
                            "message", "User not found. Cannot change the password."
                    )
            );
        }
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