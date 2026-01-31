package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.ChangeNameRequest;
import com.LuckyHub.Backend.model.ChangePasswordModel;
import com.LuckyHub.Backend.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

public interface UserService {
     User save(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    String verifyVerificationToken(String token);

    Map<String, Object> verifyLogin(UserModel userModel);
    ResponseEntity<?>resendVerifyToken(String oldToken,  final HttpServletRequest request, String url, User user);

    Optional<User> findUserByEmail(@Email(message = "Invalid email address") @NotBlank(message = "Email cannot be empty!") String email);

    void createResetPasswordToken(User user, String token);

    String GeneratePasswordResetURL(String url, String token);

    String validatePasswordToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, @NotBlank(message = "Password cannot be empty") String newPassword);

    void deletePasswordToken(String token);

    Map<String, Object> getCurrentUserFromToken(String email);

    Long findUserIdByEmail(String  email);

    Optional<User> getUserById(Long id);

    boolean changeUserName(String email, ChangeNameRequest request);

    void changeAvatar(String email, MultipartFile file);

    void updatePassword(String email, ChangePasswordModel changePasswordModel);

    User findUserByUserId(Long userId);

    ResponseCookie logoutUser(String email);

    void deleteUserAccount(String email);
}
