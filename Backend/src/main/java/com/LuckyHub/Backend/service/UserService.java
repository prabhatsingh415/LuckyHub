package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
     User save(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    String verify(String token);

    String verifyLogin(UserModel userModel);
    ResponseEntity<?>resendVerifyToken(String oldToken,  final HttpServletRequest request, String url);
}
