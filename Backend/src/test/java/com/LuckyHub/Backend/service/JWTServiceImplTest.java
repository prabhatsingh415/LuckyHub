package com.LuckyHub.Backend.service;


import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceImplTest {

    @InjectMocks
    private JWTServiceImpl jwtService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        String secret = "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=";
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);

        Subscription sub = Subscription.builder()
                .subscriptionType(SubscriptionTypes.GOLD)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        sampleUser = new User();
        sampleUser.setEmail("test@luckyhub.com");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setVerified(true);
        sampleUser.setAvatarUrl("http://avatar.com");
        sampleUser.setSubscription(sub);
    }

    // Token generation
    @Test
    void generateToken_ShouldReturnNonEmptyToken() {
        String token = jwtService.generateToken(sampleUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // Extract email from token
    @Test
    void extractUserEmail_ShouldReturnCorrectEmail() {
        String token = jwtService.generateToken(sampleUser);
        String extractedEmail = jwtService.extractUserEmail(token);

        assertEquals(sampleUser.getEmail(), extractedEmail);
    }

    // Verify all custom claims are present
    @Test
    void extractAllClaims_ShouldContainSubscriptionAndUserDetail() {
        String token = jwtService.generateToken(sampleUser);
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals(sampleUser.getEmail(), claims.getSubject());
        assertEquals("GOLD", claims.get("subscriptionType"));
        assertEquals("ACTIVE", claims.get("subscriptionStatus"));
        assertEquals(true, claims.get("isVerified"));
        assertEquals("John", claims.get("firstName"));
    }

    // Token matches UserDetails
    @Test
    void isTokenValid_ShouldReturnTrue_WhenEmailMatchesAndNotExpired() {
        String token = jwtService.generateToken(sampleUser);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(sampleUser.getEmail())
                .password("password")
                .authorities("USER")
                .build();

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    //  Token fails when email mismatches
    @Test
    void isTokenValid_ShouldReturnFalse_WhenEmailMismatches() {
        String token = jwtService.generateToken(sampleUser);

        UserDetails wrongUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("hacker@bad.com")
                .password("password")
                .authorities("USER")
                .build();

        boolean isValid = jwtService.isTokenValid(token, wrongUserDetails);

        assertFalse(isValid);
    }
}