package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.*;
import com.LuckyHub.Backend.event.ForgotPasswordEvent;
import com.LuckyHub.Backend.event.RegistrationCompleteEvent;
import com.LuckyHub.Backend.event.ResendVerificationTokenEvent;
import com.LuckyHub.Backend.exception.*;
import com.LuckyHub.Backend.model.*;
import com.LuckyHub.Backend.repository.UserRepository;
import com.LuckyHub.Backend.utils.RefreshTokenUtil;
import com.LuckyHub.Backend.utils.URLUtil;
import com.LuckyHub.Backend.utils.VerificationTokenUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ApplicationEventPublisher publisher;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;
    private final GiveawayHistoryService giveawayHistoryService;
    private final PaymentService paymentService;
    private final RateLimiterService rateLimiterService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordService passwordService;
    private final OtpService otpService;

    @Value("${ResendTokenUrl}")
    private String resendVerificationEmailURL;
    @Value("${FRONTEND_BASE_URL}")
    private String baseURL;

    @Override
    @Transactional
    public String registerNewUser(UserModel userModel) {
        String email = userModel.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("A user is already registered with this email!");
        }

        // Max Limit 5 times a day
        if(rateLimiterService.tryConsume("signUp", email, 5)){
            throw new MaximumLimitReachedException("You have reached the maximum limit for Sign up. Please try again after 24 hours.");
        }

        User user = this.save(userModel);

        String token = UUID.randomUUID().toString(); // generating token;

        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                "http://localhost:5173/verify_user",
                token
        ));

        return token;
    }

    @Override
    @Transactional
    public AuthVerificationResponse completeVerification(String token) {
        User user = verificationTokenService.validateAndGetUser(token);

        user.setVerified(true);
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        ResponseCookie refreshCookie = RefreshTokenUtil.buildRefreshCookie(refreshToken.getToken());

        return AuthVerificationResponse.builder()
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshCookies(refreshCookie)
                .build();
    }

    @Override
    @Transactional
    public TokenResponse loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));

        if (!user.isVerified()) {
            VerificationToken token = verificationTokenService.findVerificationTokenByUser(user)
                    .orElseThrow(() -> new VerificationTokenNotFoundException("Verification not found!"));

            String tokenString = token.getToken();
            throw new UserNotVerifiedException("Account not verified", user.getEmail(), tokenString);
        }

        if(rateLimiterService.tryConsume("login", user.getEmail(), 20)){
            throw new MaximumLimitReachedException("You have reached the maximum limit for logging in. Please try again after 24 hours.");
        }

       authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
        );
        // Generate JWT using the User entity
        String accessToken = jwtService.generateToken(user);
        refreshTokenService.deleteByUserId(user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        user.setUpdatedAt(new Date());

        ResponseCookie refreshCookie = RefreshTokenUtil.buildRefreshCookie(refreshToken.getToken());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshCookie(refreshCookie)
                .build();
    }

    @Override
    @Transactional
    public TokenResponse rotateRefreshToken(String refreshTokenStr) {
        RefreshToken token = refreshTokenService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));

        refreshTokenService.verifyExpiration(token);

        User user = token.getUser();
        String newAccessToken = jwtService.generateToken(user);

        refreshTokenService.deleteByUserId(user.getId());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        ResponseCookie refreshCookie = RefreshTokenUtil.buildRefreshCookie(newRefreshToken.getToken());
        log.info("Refresh token renewed successfully for user: {}", user.getEmail());

        return TokenResponse.builder()
                .refreshCookie(refreshCookie)
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String oldToken) {
        VerificationToken verificationToken = verificationTokenService.findTokenByOldToken(oldToken)
                                              .orElseThrow(() -> new InvalidTokenException("Invalid old token"));

        User user = verificationToken.getUser();
        if (user == null) throw new UserNotFoundException("User not found!");

        // Rate Limiting
        if (rateLimiterService.tryConsume("resendToken", user.getEmail(), 3)) {
            throw new MaximumLimitReachedException("Resend limit reached. Try after 24 hours.");
        }

        long diffInMillis = new Date().getTime() - verificationToken.getLastTokenSendTime().getTime();
        long diffInMinutes = diffInMillis / (1000 * 60);
        if (diffInMinutes < 15 && verificationToken.getResendCount() >= 1) {
            throw new RequestTooEarlyException("Requested too early! Wait at least 15 minutes before requesting a new verification email.");
        }


        // Update token, resend count and last send time
        String newToken = UUID.randomUUID().toString();
        verificationToken.setToken(newToken);
        verificationToken.setResendCount(verificationToken.getResendCount() + 1);
        verificationToken.setLastTokenSendTime(new Date());

        //Save
        verificationTokenService.saveToken(verificationToken);

        // resending Email
        publisher.publishEvent(new ResendVerificationTokenEvent(
                user,
                resendVerificationEmailURL,
                newToken
        ));
    }

    @Override
    public void handleForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No user found! Recheck your email."));

        // checking for maximum limit
        if(rateLimiterService.tryConsume("forgotPassword", user.getEmail(), 5)){
            throw new MaximumLimitReachedException("You have reached the maximum limit for forgot password. Please try again after 24 hours.");
        }

        String token = UUID.randomUUID().toString();
        this.createResetPasswordToken(user, token);

        String baseUrl = baseURL;
        String url = URLUtil.generatePasswordResetURL(baseUrl, token);

        log.info("Forgot Password event called!");
        //Sending Email through Event !
        publisher.publishEvent(new ForgotPasswordEvent(
                user,
                url
        ));
    }

    @Override
    @Transactional
    public void savePassword(String token, ResetPasswordRequest resetPasswordRequest) {
        PasswordToken passwordToken = passwordService.findToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid Password reset token!"));

        User user = passwordToken.getUser();

        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmNewPassword())) {
            throw new PasswordMismatchException("Passwords do not match.");
        }

        // checking for maximum limit
        if(rateLimiterService.tryConsume("resetPassword", user.getEmail(), 5)){
            throw new MaximumLimitReachedException("You have reached the maximum limit for resetting password. Please try again after 24 hours.");
        }

         if(!this.validatePasswordToken(passwordToken)){
             throw new PasswordTokenExpiredException("Resend password token expired!");
         }

        user.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        passwordService.deletePasswordToken(token);

        log.info("Password changed successfully for {}", user.getEmail());
    }

    @Override
    @Transactional
    @CacheEvict(value = "dashboardCache", key = "#email")
    public ResponseCookie processAccountDeletion(String email, String otp) {
        otpService.verifyDeleteOTP(email, otp);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        //data clean up
        giveawayHistoryService.deleteHistory(user.getId());
        paymentService.deletePayment(user.getId());
        rateLimiterService.clearLimit(user.getId());

        userRepository.delete(user);

        log.info("Complete data cleanup for user: {}", email);
        return this.logoutUser(email);
    }

    @Transactional
    @Override
    public User save(UserModel userModel){
        Subscription subscription = Subscription.builder()
                                    .subscriptionType(SubscriptionTypes.FREE)
                                    .status(SubscriptionStatus.NONE)
                                    .maxComments(SubscriptionTypes.FREE.getMaxComments())
                                    .maxWinners(SubscriptionTypes.FREE.getMaxWinners())
                                    .remainingGiveaways(SubscriptionTypes.FREE.getMaxGiveaways())
                                    .startDate(new Date())
                                    .expiringDate(null)
                                    .paymentId(null)
                                    .build();

        User user  = new User();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userModel.getPassword()));
        user.setVerified(false);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setSubscription(subscription);

        user.setAvatarUrl("https://cdn-icons-png.flaticon.com/512/4712/4712109.png");
        user.setWinnersSelectedThisMonth(0);

        subscription.setUser(user);

        userRepository.save(user);

        return user;
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        Optional<VerificationToken> existingToken =  verificationTokenService.findVerificationTokenByUser(user);

        if (existingToken.isPresent()) {
            // Update existing token and expiration time
            existingToken.get().setToken(token);
            existingToken.get().setExpirationTime(VerificationTokenUtil.calculateExpirationTime());
            verificationTokenService.saveToken(existingToken.get());
        } else {
            // Create a new verification token for first-time signup
            VerificationToken newToken = new VerificationToken(user, token, new Date());
            newToken.setExpirationTime(VerificationTokenUtil.calculateExpirationTime());
            verificationTokenService.saveToken(newToken);
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createResetPasswordToken(User user, String token) {
        // Delete old token if exists
        passwordService.findTokenByUser(user)
                       .ifPresent(passwordService::deletePasswordToken);

        //Save token
        PasswordToken passwordToken = new PasswordToken(user, token);
        passwordService.savePasswordToken(passwordToken);

        log.info("New password reset token generated for user: {}", user.getEmail());
    }

    @Override
    public boolean validatePasswordToken(PasswordToken passwordToken) {
        if (passwordToken.getExpirationTime().before(new Date())) {
            passwordService.deletePasswordToken(passwordToken.getToken());
            return false;
        }
        return true;
    }

    @Override
    @Cacheable(value = "dashboardCache", key = "#email")
    public DashboardResponse getCurrentUserFromToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Subscription sub = user.getSubscription();
        Date now = new Date();

        return DashboardResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .subscriptionType(sub.getSubscriptionType().name())
                .subscriptionStatus(sub.getStatus().name())
                .createdAt(user.getCreatedAt())
                .winnersSelectedThisMonth(user.getWinnersSelectedThisMonth())
                .maxGiveaways(sub.getSubscriptionType().getMaxGiveaways())
                .remainingGiveaways(sub.getRemainingGiveaways())
                .maxComments(sub.getMaxComments())
                .maxWinners(sub.getMaxWinners())
                .subscriptionExpiryDate(sub.getExpiringDate() != null ? sub.getExpiringDate().getTime() : null)
                .isSubscriptionExpired(sub.getExpiringDate() != null && sub.getExpiringDate().before(now))
                .build();
    }

    @Override
    public Long findUserIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "dashboardCache", key = "#email")
    public void changeUserName(String email, ChangeNameRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean isSameName = Objects.equals(user.getFirstName(), request.getFirstName()) &&
                Objects.equals(user.getLastName(), request.getLastName());

        if (isSameName) {
            return;
        }
         user.setFirstName(request.getFirstName());
         user.setLastName(request.getLastName());

         userRepository.save(user);
    }


    @Override
    @CacheEvict(value = "dashboardCache", key = "#email")
    public void changeAvatar(String email, MultipartFile file) {
        String imageUrl = imageService.uploadImage(file, "avatars");

        if (imageUrl == null || imageUrl.isBlank()) {
            log.error("Cloudinary returned an empty URL for user: {}", email);
            throw new ImageUploadFailedException("Could not retrieve image URL after upload");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setAvatarUrl(imageUrl);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void updatePassword(String email, ChangePasswordModel model) {

        if (rateLimiterService.tryConsume("changePassword", email, 3)) {
            throw new MaximumLimitReachedException("Too many failed attempts. Try again after 24 hours.");
        }

        if (!model.getNewPassword().equals(model.getConfirmNewPassword())) {
            throw new PasswordMismatchException("New password and confirmation password do not match.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!bCryptPasswordEncoder.matches(model.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect.");
        }

        if (bCryptPasswordEncoder.matches(model.getNewPassword(), user.getPassword())) {
            throw new PasswordSameAsOldException("New password cannot be the same as the old one.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(model.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated for user: {}", email);
    }

    @Override
    public User findUserByUserId(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User not found !"));
    }

    @Override
    public ResponseCookie logoutUser(String email) {
        refreshTokenService.deleteByUserEmail(email);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .build();

        SecurityContextHolder.clearContext();
        log.info("User {} logged out successfully", email);
        return cookie;
    }
}