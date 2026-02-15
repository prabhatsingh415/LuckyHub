package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.service.GoogleAuthService;
import com.LuckyHub.Backend.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoogleAuthController.class)
class GoogleAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private GoogleAuthService googleAuthService;

    @MockitoBean private JWTService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    @Autowired
    private GoogleAuthController googleAuthController;

    private final String mockSuccessUrl = "http://localhost:5173/dashboard";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleAuthController, "frontendSuccessUrl", mockSuccessUrl);
    }

    @Test
    @WithMockUser
    void handleGoogleCallback_ShouldRedirectWithCookie() throws Exception {
        ResponseCookie mockCookie = ResponseCookie.from("refreshToken", "google-refresh-uuid")
                .path("/")
                .httpOnly(true)
                .build();

        when(googleAuthService.authenticateGoogleUser(anyString())).thenReturn(mockCookie);

        mockMvc.perform(get("/auth/google/callback")
                        .param("code", "mock-google-code"))
                        .andExpect(status().isFound())
                        .andExpect(header().string(HttpHeaders.LOCATION, mockSuccessUrl))
                        .andExpect(header().exists(HttpHeaders.SET_COOKIE));
    }
}