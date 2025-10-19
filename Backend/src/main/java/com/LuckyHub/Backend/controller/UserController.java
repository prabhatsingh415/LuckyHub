package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.exception.RefreshTokenNotFound;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.PasswordModel;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.service.JWTService;
import com.LuckyHub.Backend.service.RefreshTokenService;
import com.LuckyHub.Backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;

    public UserController(UserService userService, ApplicationEventPublisher publisher, RefreshTokenService refreshTokenService, JWTService jwtService) {
        this.userService = userService;
        this.publisher = publisher;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody  UserModel userModel,
                                                      final HttpServletRequest request){
       User user = userService.save(userModel);

        if(user == null){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "status", "failed",
                            "message", "A user is already registered with this email!"
                    ));
        }

       //Event for sending mail
       publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                "http://localhost:5173/verify_user"
       ));

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User registered successfully. Please verify your email.",
                "user", user
        ));
    }

    //For verifying user through E-Mail
    @GetMapping("/verifyRegistration")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token){
      if(userService.verifyVerificationToken(token).equalsIgnoreCase("Valid")){
          String email = jwtService.extractUserEmail(token);
          Optional<User> user = userService.findUserByEmail(email);

          if(user.isEmpty()){
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                      Map.of(
                              "status", "success",
                              "message", "Account activated!"
                            )
              );
          }

          UserModel userModel = userService.convertToUserModel(user.get());
          Map<String, Object> tokens = userService.verifyLogin(userModel);

          String accessToken = tokens.getOrDefault("accessToken", "").toString();
          String refreshToken = tokens.getOrDefault("refreshToken", "").toString();

          ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                  .httpOnly(true)
                  .secure(true)
                  .path("/")
                  .maxAge(14 * 24 * 60 * 60)
                  .sameSite("Strict")
                  .build();

          HttpHeaders resHeaders = new HttpHeaders();
          resHeaders.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

          return ResponseEntity.ok(Map.of(
                  "status", "success",
                  "message", "Account activated!",
                  "accessToken", accessToken
          ));

      }
      return ResponseEntity.badRequest().body(
              Map.of(
                      "status", "Failed",
                      "message", "Unable to activate Account !"
              )
      );
    }

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody UserModel userModel) {

       Map<String, Object> data = userService.verifyLogin(userModel);

        // Return JSON response
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        // Extract refresh token from cookie
        String refreshTokenStr = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshTokenStr = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshTokenStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token is missing"));
        }

        // Validate the refresh token
        RefreshToken token = refreshTokenService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RefreshTokenNotFound("Refresh token not found"));

        refreshTokenService.verifyExpiration(token);

        // Generate new access token
        String newAccessToken = jwtService.generateToken(token.getUser());

        // Delete old refresh token & create a new one
        refreshTokenService.deleteByUserId(token.getUser().getId());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(token.getUser().getEmail());

        // Set new refresh token as HttpOnly cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(14 * 24 * 60 * 60) // 14 days
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("accessToken", newAccessToken));
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
        String url;
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

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Missing or invalid Authorization header"));
            }
            String token = authHeader.substring(7);

            Map<String, Object> userData = userService.getCurrentUserFromToken(token);

            return ResponseEntity.ok(Map.of("status", "success", "user", userData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
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