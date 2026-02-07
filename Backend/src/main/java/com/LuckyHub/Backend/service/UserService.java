package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.PasswordToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseCookie;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {

    User save(UserModel userModel, boolean isVerified);

    void saveVerificationTokenForUser(User user, String token);

    Optional<User> findUserByEmail(@Email(message = "Invalid email address") @NotBlank(message = "Email cannot be empty!") String email);

    void createResetPasswordToken(User user, String token);

    boolean validatePasswordToken(PasswordToken passwordToken);

    DashboardResponse getCurrentUserFromToken(String email);

    Optional<User> getUserById(Long id);

    void changeUserName(String email, ChangeNameRequest request);

    void changeAvatar(String email, MultipartFile file);

    void updatePassword(String email, ChangePasswordModel changePasswordModel);

    User findUserByUserId(Long userId);

    ResponseCookie logoutUser(String email);

    String registerNewUser(@Valid UserModel userModel);

    AuthVerificationResponse completeVerification(String token);

    TokenResponse loginUser(LoginRequest loginRequest);

    TokenResponse rotateRefreshToken(String refreshTokenStr);

    void resendVerificationEmail(String oldToken);

    void handleForgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    void savePassword(String token, ResetPasswordRequest resetPasswordRequest);

    ResponseCookie processAccountDeletion(String email, String otp);

    void saveUser(User user);

    Long findUserIdByEmail(String email);

    void resetAllWinnersCount();
}

