package com.LuckyHub.Backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.LuckyHub.Backend.entity.PasswordToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.repository.PasswordTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {

    @Mock
    private PasswordTokenRepository repository;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    private PasswordToken sampleToken;
    private User sampleUser;
    private final String rawToken = "reset-token-xyz-123";

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("user@luckyhub.com");

        sampleToken = new PasswordToken();
        sampleToken.setToken(rawToken);
        sampleToken.setUser(sampleUser);
    }

    // Verifies finding a token by the raw token
    @Test
    void findToken_ShouldReturnToken_WhenTokenExists() {
        when(repository.findByToken(rawToken)).thenReturn(Optional.of(sampleToken));

        Optional<PasswordToken> result = passwordService.findToken(rawToken);

        assertTrue(result.isPresent());
        assertEquals(rawToken, result.get().getToken());
        verify(repository).findByToken(rawToken);
    }

    // Verifies behavior when token is missing in DB
    @Test
    void findToken_ShouldReturnEmpty_WhenTokenDoesNotExist() {
        when(repository.findByToken(anyString())).thenReturn(Optional.empty());

        Optional<PasswordToken> result = passwordService.findToken("invalid-token");

        assertFalse(result.isPresent());
    }

    // Verifies deletion by raw token string
    @Test
    void deletePasswordToken_ByString_ShouldInvokeRepository() {
        passwordService.deletePasswordToken(rawToken);
        verify(repository).deleteByToken(rawToken);
    }

    // Verifies saving/updating a password token
    @Test
    void savePasswordToken_ShouldInvokeRepository() {
        passwordService.savePasswordToken(sampleToken);
        verify(repository).save(sampleToken);
    }

    // Verifies deletion by token object
    @Test
    void deletePasswordToken_ByObject_ShouldInvokeRepository() {
        passwordService.deletePasswordToken(sampleToken);
        verify(repository).delete(sampleToken);
    }

    // Verifies finding a token associated with a specific user
    @Test
    void findTokenByUser_ShouldReturnToken_WhenUserHasOne() {
        when(repository.findByUser(sampleUser)).thenReturn(Optional.of(sampleToken));

        Optional<PasswordToken> result = passwordService.findTokenByUser(sampleUser);

        assertTrue(result.isPresent());
        assertEquals(sampleUser, result.get().getUser());
        verify(repository).findByUser(sampleUser);
    }
}