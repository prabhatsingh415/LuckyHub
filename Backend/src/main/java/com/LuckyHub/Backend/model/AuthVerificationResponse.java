package com.LuckyHub.Backend.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@Builder
public class AuthVerificationResponse {
    private final String email;
    private final String accessToken;
    private final ResponseCookie refreshCookies;
}
