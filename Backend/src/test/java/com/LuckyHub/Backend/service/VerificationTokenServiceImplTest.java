package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.exception.InvalidTokenException;
import com.LuckyHub.Backend.exception.VerificationTokenExpiredException;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceImplTest {

    @Mock
    private VerificationTokenRepository repository;

    @InjectMocks
    private VerificationTokenServiceImpl service;

    private User sampleUser;
    private VerificationToken sampleToken;
    private final String rawToken = "valid-token-123";

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setEmail("user@luckyhub.com");

        sampleToken = new VerificationToken();
        sampleToken.setToken(rawToken);
        sampleToken.setUser(sampleUser);

        sampleToken.setExpirationTime(new Date(System.currentTimeMillis() + 3600000));
    }

    // Returns user when token is valid and not expired
    @Test
    void validateAndGetUser_ShouldReturnUser_WhenTokenIsValid() {
        when(repository.findByToken(rawToken)).thenReturn(Optional.of(sampleToken));

        User result = service.validateAndGetUser(rawToken);

        assertNotNull(result);
        assertEquals(sampleUser.getEmail(), result.getEmail());
        verify(repository, never()).delete(any());
    }

    // Throws exception when token is not found in DB
    @Test
    void validateAndGetUser_ShouldThrowException_WhenTokenIsInvalid() {
        when(repository.findByToken("wrong-token")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () ->
                service.validateAndGetUser("wrong-token")
        );
    }

    // Deletes token and throws exception when expired
    @Test
    void validateAndGetUser_ShouldDeleteAndThrow_WhenTokenIsExpired() {
        sampleToken.setExpirationTime(new Date(System.currentTimeMillis() - 1000));
        when(repository.findByToken(rawToken)).thenReturn(Optional.of(sampleToken));

        assertThrows(VerificationTokenExpiredException.class, () ->
                service.validateAndGetUser(rawToken)
        );

        verify(repository).delete(sampleToken);
    }

    // Verifies finding token by User
    @Test
    void findVerificationTokenByUser_ShouldReturnToken() {
        when(repository.findByUser(sampleUser)).thenReturn(Optional.of(sampleToken));

        Optional<VerificationToken> result = service.findVerificationTokenByUser(sampleUser);

        assertTrue(result.isPresent());
        assertEquals(rawToken, result.get().getToken());
    }

    // Verifies finding token by old token
    @Test
    void findTokenByOldToken_ShouldReturnToken(){
        sampleToken.setToken("oldToken");
        when(repository.findByToken("oldToken")).thenReturn(Optional.of(sampleToken));

        Optional<VerificationToken> result = service.findTokenByOldToken("oldToken");

        assertTrue(result.isPresent());
        assertEquals("oldToken", result.get().getToken());
    }


    // Verifies repository save call
    @Test
    void saveToken_ShouldInvokeRepository() {
        service.saveToken(sampleToken);
        verify(repository).save(sampleToken);
    }
}