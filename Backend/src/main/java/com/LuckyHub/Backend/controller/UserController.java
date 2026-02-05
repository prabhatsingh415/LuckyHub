package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.exception.RefreshTokenNotFoundException;
import com.LuckyHub.Backend.model.*;
import com.LuckyHub.Backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OtpService otpService;


    // -------------------- SIGNUP --------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid  @RequestBody UserModel userModel) {
        String email = userModel.getEmail();
        log.info("Signup attempt for email: {}", email);

        String token = userService.registerNewUser(userModel);

        log.info("Signup successful: Verification mail sent to email -{}", email);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User registered successfully. Please verify your email.",
                "token", token,
                "email", email
        ));
    }

    // -------------------- VERIFY EMAIL --------------------
    @GetMapping("/verifyRegistration")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        AuthVerificationResponse response = userService.completeVerification(token);

        String email = response.getEmail();
        String accessToken = response.getAccessToken();
        ResponseCookie refreshCookie = response.getRefreshCookies();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "status", "success",
                        "message", "Account activated!",
                        "accessToken", accessToken,
                        "email", email
                ));
    }


    // -------------------- LOGIN --------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getEmail());

        TokenResponse tokenResponse = userService.loginUser(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,  tokenResponse.getRefreshCookie().toString())
                .body(Map.of(
                        "status", "success",
                        "accessToken", tokenResponse.getAccessToken()
                ));
    }

    // -------------------- REFRESH TOKEN --------------------
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        log.info("Refresh token rotation requested");

        if (refreshToken == null) {
            throw new RefreshTokenNotFoundException("Refresh token is missing from cookies");
        }

        TokenResponse response = userService.rotateRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getRefreshCookie().toString())
                .body(Map.of("status" , "success",
                        "accessToken", response.getAccessToken()));
    }

    // -------------------- RESEND TOKEN --------------------
    @GetMapping("/resendToken")
    public ResponseEntity<?> resendVerificationToken(@RequestParam("token") String oldToken) {
        log.info("Resend verification token request received");

        userService.resendVerificationEmail(oldToken);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Verification email resent successfully!"
        ));
    }

    // -------------------- FORGOT PASSWORD --------------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        userService.handleForgotPassword(forgotPasswordRequest);

        log.info("Password reset link generated for {}", forgotPasswordRequest.getEmail());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Reset password link has been sent to the user's email!"
        ));
    }

    // -------------------- SAVE PASSWORD --------------------
    @PostMapping("/reset-password-confirm")
    public ResponseEntity<?> savePassword(@RequestParam("token") String token,
                                          @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        userService.savePassword(token, resetPasswordRequest);

        log.info("Password changed successfully for {}", resetPasswordRequest.getEmail());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Password has been reset successfully."
        ));
    }

    // -------------------- GET CURRENT USER --------------------
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        DashboardResponse userData = userService.getCurrentUserFromToken(email);

        log.info("Fetched current user data successfully");
        return ResponseEntity.ok(Map.of("status", "success", "user", userData));
    }

    // -------------------- Change User Name --------------------
    @PutMapping("/updateName")
    public ResponseEntity<?> changeUserName(
            @RequestBody ChangeNameRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        userService.changeUserName(userDetails.getUsername(), request);

        log.info("Name updated successfully for: {}", userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Name updated successfully"));
    }

    // -------------------- Change Avatar --------------------
    @PutMapping("/updateAvatar")
    public ResponseEntity<?> changeAvatar(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        userService.changeAvatar(email, file);

        return ResponseEntity.ok(
                Map.of("message", "Image uploaded successfully.")
        );
    }

    // -------------------- Change Password --------------------

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordModel model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        userService.updatePassword(email, model);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully.")
        );
    }

    // ---------------- Log out ---------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetails userDetails){
        ResponseCookie cookie = userService.logoutUser(userDetails.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("status", "success", "message", "Logged out!"));
    }


    //------------ Delete Account Request ---------
    @PostMapping("/request-delete")
    public ResponseEntity<?> requestDeletion(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        otpService.sendDeleteAccountOTP(email);
        return ResponseEntity.ok(Map.of("message", "Verification code sent to email"));
    }

    //----------- Delete Account Confirm ---------
    @DeleteMapping("/confirm-delete")
    public ResponseEntity<?> confirmDeletion(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String otp) {

        String email = userDetails.getUsername();

        ResponseCookie logoutCookie = userService.processAccountDeletion(email, otp);

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
                .body(Map.of("status", "success", "message", "Account Deleted Successfully!"));
    }
}