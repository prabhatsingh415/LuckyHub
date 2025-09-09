package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
     User save(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    String verify(String token);

    String verifyLogin(UserModel userModel);
    ResponseEntity<?>resendVerifyToken(String oldToken,  final HttpServletRequest request, String url);

    Optional<User> findUserByEmail(@Email(message = "Invalid email address") @NotBlank(message = "Email cannot be empty!") String email);

    void createResendPasswordToken(User user, String token);

    String GeneratePasswordResetURL(String url, String token);

    String validatePasswordToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, @NotBlank(message = "Password cannot be empty") String newPassword);

    void deletePasswordToken(String token);
}
