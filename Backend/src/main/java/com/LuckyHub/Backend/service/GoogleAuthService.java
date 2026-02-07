package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.model.TokenResponse;
import org.springframework.http.ResponseCookie;

import java.util.Map;

public interface GoogleAuthService {
    RefreshToken processUser(String code);

    ResponseCookie authenticateGoogleUser(String code);
}
