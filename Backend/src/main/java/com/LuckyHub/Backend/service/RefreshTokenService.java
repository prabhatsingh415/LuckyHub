package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String email);
    Optional<RefreshToken> findByToken(String token);
    void verifyExpiration(RefreshToken token);
    void deleteByUserId(Long userId);
    void deleteByUserEmail(String email);
}
