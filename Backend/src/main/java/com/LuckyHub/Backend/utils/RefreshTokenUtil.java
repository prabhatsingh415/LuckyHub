package com.LuckyHub.Backend.utils;

import org.springframework.http.ResponseCookie;

public class RefreshTokenUtil {

    private RefreshTokenUtil() {
        // Prevent instantiation
    }

    public static ResponseCookie buildRefreshCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(14 * 24 * 60 * 60) // 14 days
                .build();
    }
}
