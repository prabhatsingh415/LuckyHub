package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.GoogleAuthenticationFailedException;
import com.LuckyHub.Backend.exception.UnauthorizedException;
import com.LuckyHub.Backend.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceImplTest {

    @Mock private RefreshTokenService refreshTokenService;
    @Mock private RestTemplate restTemplate;
    @Mock private UserService userService;

    @InjectMocks
    private GoogleAuthServiceImpl googleAuthService;

    private final String authCode = "mock-auth-code";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleAuthService, "clientId", "mockId");
        ReflectionTestUtils.setField(googleAuthService, "clientSecret", "mockSecret");
        ReflectionTestUtils.setField(googleAuthService, "redirectURL", "http://localhost:8080");
    }

    //  Process existing user
    @Test
    void processUser_ShouldReturnToken_ForExistingUser() {
        Map<String, String> tokenBody = new HashMap<>();
        tokenBody.put("id_token", "mock-id-token");
        ResponseEntity<Map> tokenResponse = new ResponseEntity<>(tokenBody, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(tokenResponse);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", "test@gmail.com");
        userInfo.put("given_name", "John");
        userInfo.put("family_name", "Doe");
        ResponseEntity<Map> userInfoResponse = new ResponseEntity<>(userInfo, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(userInfoResponse);

        User existingUser = new User();
        existingUser.setEmail("test@gmail.com");
        when(userService.findUserByEmail("test@gmail.com")).thenReturn(Optional.of(existingUser));

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("refresh-123");
        when(refreshTokenService.createRefreshToken(existingUser)).thenReturn(mockRefreshToken);

        RefreshToken result = googleAuthService.processUser(authCode);

        assertEquals("refresh-123", result.getToken());
        verify(userService, never()).save(any(), anyBoolean());
    }

    //Create and process new user
    @Test
    void processUser_ShouldCreateNewUser_WhenNotExists() {
        Map<String, String> tokenBody = Map.of("id_token", "mock-id-token");
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(tokenBody, HttpStatus.OK));

        Map<String, Object> userInfo = Map.of("email", "new@gmail.com");
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(userInfo, HttpStatus.OK));

        when(userService.findUserByEmail("new@gmail.com")).thenReturn(Optional.empty());
        User newUser = new User();
        when(userService.save(any(UserModel.class), eq(true))).thenReturn(newUser);
        when(refreshTokenService.createRefreshToken(newUser)).thenReturn(new RefreshToken());

        googleAuthService.processUser(authCode);

        verify(userService).save(any(UserModel.class), eq(true));
    }

    // Google Info API returns error
    @Test
    void processUser_ShouldThrowUnauthorized_WhenGoogleFails() {
        Map<String, String> tokenBody = Map.of("id_token", "mock-id-token");
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(tokenBody, HttpStatus.OK));

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        assertThrows(UnauthorizedException.class, () -> googleAuthService.processUser(authCode));
    }

    // Generic Authentication Error
    @Test
    void authenticateGoogleUser_ShouldThrowCustomException_OnAnyError() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Network Error"));

        assertThrows(GoogleAuthenticationFailedException.class, () ->
                googleAuthService.authenticateGoogleUser(authCode)
        );
    }
}