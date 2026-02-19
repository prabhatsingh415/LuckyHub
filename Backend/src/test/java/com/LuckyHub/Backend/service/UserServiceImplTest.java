package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.exception.PasswordMismatchException;
import com.LuckyHub.Backend.exception.UserAlreadyExistsException;
import com.LuckyHub.Backend.exception.UserNotVerifiedException;
import com.LuckyHub.Backend.model.ChangePasswordModel;
import com.LuckyHub.Backend.model.LoginRequest;
import com.LuckyHub.Backend.model.TokenResponse;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private JWTServiceImpl jwtService;
    @Mock private ApplicationEventPublisher publisher;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private GiveawayHistoryService giveawayHistoryService;
    @Mock private PaymentService paymentService;
    @Mock private RateLimiterService rateLimiterService;
    @Mock private VerificationTokenService verificationTokenService;
    @Mock private OtpService otpService;
    @Mock private CacheManager cacheManager;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserModel sampleModel;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "resendVerificationEmailURL", "http://test.com");
        ReflectionTestUtils.setField(userService, "baseURL", "http://frontend.com");

        sampleModel = new UserModel();
        sampleModel.setEmail("test@luckyhub.com");
        sampleModel.setPassword("pass123");
        sampleModel.setFirstName("John");

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@luckyhub.com");
        sampleUser.setVerified(true);
    }

    // Registration
    @Test
    void registerNewUser_ShouldSaveUserAndPublishEvent() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(rateLimiterService.tryConsume(anyString(), anyString(), anyInt())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encoded_pass");

        String token = userService.registerNewUser(sampleModel);

        assertNotNull(token);
        verify(userRepository).save(any(User.class));
        verify(publisher).publishEvent(any(RegistrationCompleteEvent.class));
    }

    // Registration Failure - User already exists
    @Test
    void registerNewUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerNewUser(sampleModel));
    }

    // Login
    @Test
    void loginUser_ShouldReturnTokens_WhenValid() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(rateLimiterService.tryConsume(anyString(), anyString(), anyInt())).thenReturn(false);
        when(jwtService.generateToken(any())).thenReturn("access-token");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("refresh-uuid");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(mockRefreshToken);

        TokenResponse response = userService.loginUser(new LoginRequest("test@luckyhub.com", "pass123"));

        assertEquals("access-token", response.getAccessToken());
        verify(authenticationManager).authenticate(any());
    }

    // Login Failure
    @Test
    void loginUser_ShouldThrowException_WhenNotVerified() {
        sampleUser.setVerified(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(verificationTokenService.findVerificationTokenByUser(any())).thenReturn(Optional.of(new VerificationToken()));

        assertThrows(UserNotVerifiedException.class, () ->
                userService.loginUser(new LoginRequest("test@luckyhub.com", "pass123"))
        );
    }

    // Account Deletion
    @Test
    void processAccountDeletion_ShouldCleanupDataAndDeleteUser() {
        String email = "test@luckyhub.com";
        String otp = "123456";
        when(cacheManager.getCache("historyCache")).thenReturn(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        userService.processAccountDeletion(email, otp);

        verify(otpService).verifyDeleteOTP(email, otp);
        verify(giveawayHistoryService).deleteHistory(sampleUser.getId());
        verify(paymentService).deletePayment(sampleUser.getId());
        verify(rateLimiterService).clearLimit(sampleUser.getId());
        verify(userRepository).delete(sampleUser);
    }

    // Update Password
    @Test
    void updatePassword_ShouldThrowException_WhenPasswordsMismatch() {
        ChangePasswordModel model = new ChangePasswordModel();
        model.setNewPassword("new");
        model.setConfirmNewPassword("mismatch");

        assertThrows(PasswordMismatchException.class, () ->
                userService.updatePassword("test@luckyhub.com", model)
        );
    }
}