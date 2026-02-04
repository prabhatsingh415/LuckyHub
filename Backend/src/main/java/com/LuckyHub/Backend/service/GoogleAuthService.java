package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;

import java.util.Map;

public interface GoogleAuthService {
    RefreshToken processUser(String code);
}
