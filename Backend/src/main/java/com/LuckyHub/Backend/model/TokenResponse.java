package com.LuckyHub.Backend.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@Builder
public class TokenResponse {
    private final String accessToken;
    private final ResponseCookie refreshCookie;
}
