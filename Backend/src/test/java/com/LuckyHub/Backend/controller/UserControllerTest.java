package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.model.*;
import com.LuckyHub.Backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private UserService userService;
    @MockitoBean private OtpService otpService;

    @MockitoBean private JWTService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    // Checks request processing and JSON response
    @Test
    @WithMockUser
    void signUp_ShouldReturnSuccess_WhenDataIsValid() throws Exception {
        when(userService.registerNewUser(any(UserModel.class))).thenReturn("mock-token-123");

        mockMvc.perform(post("/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@luckyhub.com\", \"password\":\"Password@123\", \"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.token").value("mock-token-123"));
    }

    // Verifies JWT in body and Refresh Token in Cookie
    @Test
    @WithMockUser
    void login_ShouldReturnAccessTokenAndCookie() throws Exception {
        ResponseCookie mockCookie = ResponseCookie.from("refreshToken", "uuid-123").path("/").build();
        TokenResponse mockResponse = TokenResponse.builder()
                                                  .accessToken("access-123")
                                                  .refreshCookie(mockCookie)
                                                  .build();

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/user/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@luckyhub.com\", \"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.accessToken").value("access-123"));
    }

    // Verifies @AuthenticationPrincipal handling
    @Test
    @WithMockUser(username = "test@luckyhub.com")
    void getMe_ShouldReturnDashboardData() throws Exception {
        DashboardResponse mockDashboard = DashboardResponse.builder()
                .email("test@luckyhub.com")
                .firstName("John")
                .subscriptionType("GOLD")
                .build();

        when(userService.getCurrentUserFromToken("test@luckyhub.com")).thenReturn(mockDashboard);

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.user.subscriptionType").value("GOLD"));
    }
}