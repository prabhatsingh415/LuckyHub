package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.RefreshTokenExpiredException;
import com.LuckyHub.Backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User sampleUser;
    private RefreshToken sampleToken;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("user@luckyhub.com");

        sampleToken = new RefreshToken();
        sampleToken.setUser(sampleUser);
        sampleToken.setToken("existing-uuid-token");
        sampleToken.setExpiryDate(Instant.now().plus(14, ChronoUnit.DAYS));
    }

    // Creates a brand-new token if none exists
    @Test
    void createRefreshToken_ShouldCreateNew_WhenNoneExists() {
        when(repository.findByUser(sampleUser)).thenReturn(Optional.empty());
        when(repository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        RefreshToken result = refreshTokenService.createRefreshToken(sampleUser);

        assertNotNull(result.getToken());
        assertEquals(sampleUser, result.getUser());
        verify(repository).save(any(RefreshToken.class));
    }

    // Updates existing token instead of creating a new row
    @Test
    void createRefreshToken_ShouldUpdateExisting_WhenTokenExists() {
        String oldTokenString = sampleToken.getToken();
        when(repository.findByUser(sampleUser)).thenReturn(Optional.of(sampleToken));
        when(repository.save(any(RefreshToken.class))).thenReturn(sampleToken);

        RefreshToken result = refreshTokenService.createRefreshToken(sampleUser);

        assertNotEquals(oldTokenString, result.getToken());
        assertEquals(sampleToken, result);
        verify(repository).save(sampleToken);
    }

    // Verification passes for non-expired token
    @Test
    void verifyExpiration_ShouldNotThrowException_WhenTokenIsValid() {
        assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(sampleToken));
    }

    // Throws exception and DELETES from DB if expired
    @Test
    void verifyExpiration_ShouldDeleteAndThrow_WhenTokenIsExpired() {
        sampleToken.setExpiryDate(Instant.now().minus(1, ChronoUnit.DAYS));

        assertThrows(RefreshTokenExpiredException.class, () ->
                refreshTokenService.verifyExpiration(sampleToken)
        );

        verify(repository).delete(sampleToken);
    }

    // Throws exception if token object is null
    @Test
    void verifyExpiration_ShouldThrowException_WhenTokenIsNull() {
        assertThrows(RefreshTokenExpiredException.class, () ->
                refreshTokenService.verifyExpiration(null)
        );
    }

    // Verifies deletion by User ID
    @Test
    void deleteByUser_ShouldInvokeRepository() {
        refreshTokenService.deleteByUser(sampleUser);
        verify(repository).deleteByUserId(sampleUser.getId());
    }
}