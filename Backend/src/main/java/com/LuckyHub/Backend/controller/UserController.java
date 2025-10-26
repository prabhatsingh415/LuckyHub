package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.event.ForgotPasswordEvent;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.exception.RefreshTokenNotFound;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.PasswordModel;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import com.LuckyHub.Backend.service.JWTService;
import com.LuckyHub.Backend.service.RefreshTokenService;
import com.LuckyHub.Backend.service.UserService;
import com.LuckyHub.Backend.utils.RefreshTokenUtil;
import com.LuckyHub.Backend.utils.UrlUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;

    // -------------------- SIGNUP --------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel, final HttpServletRequest request) {
        log.info("Signup attempt for email: {}", userModel.getEmail());
        User user = userService.save(userModel);

        if (user == null) {
            log.warn("Signup failed: Email already exists - {}", userModel.getEmail());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failed",
                    "message", "A user is already registered with this email!"
            ));
        }

        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                "http://localhost:5173/verify_user"
        ));

        log.info("Signup successful: Verification mail sent to {}", user.getEmail());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User registered successfully. Please verify your email.",
                "user", user
        ));
    }

    // -------------------- VERIFY EMAIL --------------------
    @GetMapping("/verifyRegistration")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null)
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Failed",
                    "message", "Invalid verification link!"
            ));

        String result = userService.verifyVerificationToken(token);
        if (!result.equalsIgnoreCase("Valid"))
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Failed",
                    "message", result
            ));

        User user = verificationToken.getUser();

        // Generate JWT and Refresh Token
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        ResponseCookie refreshCookie = RefreshTokenUtil.buildRefreshCookie(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "status", "success",
                        "message", "Account activated!",
                        "accessToken", accessToken
                ));
    }


    // -------------------- LOGIN --------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserModel userModel) {
        log.info("Login attempt for user: {}", userModel.getEmail());
        Map<String, Object> data = userService.verifyLogin(userModel);

        String refreshToken = data.getOrDefault("refreshToken", "").toString();
        ResponseCookie refreshCookie = RefreshTokenUtil.buildRefreshCookie(refreshToken);

        log.info("Login successful for user: {}", userModel.getEmail());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "status", "success",
                        "accessToken", data.get("accessToken")
                ));
    }

    // -------------------- REFRESH TOKEN --------------------
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
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
            log.warn("Refresh token missing in cookies");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token is missing"));
        }

        RefreshToken token = refreshTokenService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RefreshTokenNotFound("Refresh token not found"));

        refreshTokenService.verifyExpiration(token);
        String newAccessToken = jwtService.generateToken(token.getUser());

        refreshTokenService.deleteByUserId(token.getUser().getId());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(token.getUser().getEmail());

        ResponseCookie refreshCookie = RefreshTokenUtil.buildRefreshCookie(newRefreshToken.getToken());
        log.info("Refresh token renewed successfully for user: {}", token.getUser().getEmail());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("accessToken", newAccessToken));
    }

    // -------------------- RESEND TOKEN --------------------
    @GetMapping("/resendToken")
    public ResponseEntity<?> resendVerificationToken(@RequestParam("token") String oldToken,
                                                     final HttpServletRequest request) {
        if (oldToken == null || oldToken.isBlank()) {
            log.warn("Resend verification token failed: Empty token");
            return ResponseEntity.badRequest().body(Map.of("error", "Token must not be empty"));
        }

        String baseUrl = UrlUtil.buildBaseUrl(request);
        log.info("Resending verification token for {}", baseUrl);
        return userService.resendVerifyToken(oldToken, request, baseUrl);
    }

    // -------------------- FORGOT PASSWORD --------------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        Optional<User> user = userService.findUserByEmail(passwordModel.getEmail());

        if(user.isEmpty()) {
            log.error("Password reset failed: User not found for {}", passwordModel.getEmail());
            throw new UserNotFoundException("No user found! Recheck your email.");
        }

        String token = UUID.randomUUID().toString();
        userService.createResetPasswordToken(user.get(), token);

        String baseUrl = System.getenv("FRONTEND_BASE_URL");
        String url = userService.GeneratePasswordResetURL(baseUrl, token);

        log.info("Forgot Password event called!");
        //Sending Email through Event !
        publisher.publishEvent(new ForgotPasswordEvent(
                user.get(),
                url
        ));

        log.info("Password reset link generated for {}", passwordModel.getEmail());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Reset password link has been sent to the user's email!"
        ));
    }

    // -------------------- SAVE PASSWORD --------------------
    @PostMapping("/reset-password-confirm")
    public ResponseEntity<?> savePassword(@RequestParam("token") String token,
                                          @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordToken(token);

        if (!"Valid".equalsIgnoreCase(result)) {
            log.warn("Invalid or expired password reset token: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid or expired token"
            ));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            userService.deletePasswordToken(token);
            log.info("Password changed successfully for {}", user.get().getEmail());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Password has been reset successfully."
            ));
        }

        log.error("Password change failed: User not found for token {}", token);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", "error",
                "message", "User not found. Cannot change the password."
        ));
    }

    // -------------------- GET CURRENT USER --------------------
    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Missing or invalid Authorization header"));
            }

            String token = authHeader.substring(7);
            Map<String, Object> userData = userService.getCurrentUserFromToken(token);
            log.info("Fetched current user data successfully");
            return ResponseEntity.ok(Map.of("status", "success", "user", userData));

        } catch (Exception e) {
            log.error("Error fetching current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
