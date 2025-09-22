package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.*;
import com.LuckyHub.Backend.event.ResendVerificationTokenEvent;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.repository.PasswordTokenRepository;
import com.LuckyHub.Backend.repository.UserRepository;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class UserServiceimpl implements UserService{

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ApplicationEventPublisher publisher;
    private final PasswordTokenRepository passwordTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PaymentService paymentService;

    public UserServiceimpl(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, ApplicationEventPublisher publisher, PasswordTokenRepository passwordTokenRepository, RefreshTokenService refreshTokenService, PaymentService paymentService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.publisher = publisher;
        this.passwordTokenRepository = passwordTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.paymentService = paymentService;
    }

    public User save(UserModel userModel){
        Subscription subscription = Subscription.builder()
                                    .subscriptionType(SubscriptionTypes.FREE)
                                    .status(SubscriptionStatus.NONE)
                                    .maxComments(SubscriptionTypes.FREE.getMaxComments())
                                    .maxWinners(SubscriptionTypes.FREE.getMaxWinners())
                                    .remainingGiveaways(SubscriptionTypes.FREE.getMaxGiveaways())
                                    .startDate(null)
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

        user.setAvatarUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        user.setWinnersSelectedThisMonth(0);

        subscription.setUser(user);

        userRepository.save(user);

        return user;
    }

    @Override
    public Map<String, Object> verifyLogin(UserModel userModel) {
        // Authenticate user
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userModel.getEmail(),
                        userModel.getPassword()
                )
        );

        // Load the User entity from DB
        User user = userRepository.findByEmail(userModel.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate JWT using the User entity
        String accessToken = jwtService.generateToken(user);

        refreshTokenService.deleteByUserId(user.getId());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        user.setUpdatedAt(new Date());
        long expiresIn = 10 * 60;

        return Map.of(
                "status", "success",
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken(),
                "expiresIn", expiresIn
        );
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        // Check if a token already exists for this user
        VerificationToken existingToken = verificationTokenRepository.findByUser(user);

        if (existingToken != null) {
            // Update existing token and expiration time
            existingToken.setToken(token);
            existingToken.setExpirationTime(calculateExpirationTime());
            verificationTokenRepository.save(existingToken); // update
        } else {
            // No existing token → throw exception or handle as needed
            throw new IllegalStateException("No existing verification token found for user: " + user.getEmail());
        }
    }

    // Utility method to calculate expiration
    private Date calculateExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10); // expiration 10 minutes
        return calendar.getTime();
    }

    @Override
    public String verifyVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if(verificationToken == null){
            System.out.println("Token is INVALID because its null !");
            return "Invalid Token !";
        }

        Calendar calendar = Calendar.getInstance();

        if (verificationToken.getExpirationTime().before(calendar.getTime())) {
            System.out.println("Verification token is Expired: " + verificationToken);
            verificationTokenRepository.delete(verificationToken);
            return "Token Expired !";
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        return "Valid";
    }  //Signup Verification

    @Override
    public ResponseEntity<?> resendVerifyToken(String oldToken, final HttpServletRequest request, String url) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);

        if(verificationToken == null){
            return ResponseEntity
                    .badRequest()
                    .body(
                        Map.of(
                                "Error",
                                "Invalid Old Token , Try Sign up again !"
                        )
                    );
        }

        User user =  verificationToken.getUser();

        // resending Email using the Event and Event Listener
        publisher.publishEvent(new ResendVerificationTokenEvent(
                user,
                url
        ));

        return ResponseEntity.ok(Map.of(
                "message", "Verification link resent to your email"
        ));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createResendPasswordToken(User user, String token) {
        PasswordToken passwordToken = new PasswordToken(user, token);
        passwordTokenRepository.save(passwordToken);
    }

    @Override
    public String GeneratePasswordResetURL(String url, String token) {
        return url
                + "/savePassword?token="
                + token;
    }

    @Override
    public String validatePasswordToken(String token) {
        PasswordToken  passwordToken = passwordTokenRepository.findByToken(token);

        if(passwordToken == null){
            return "Invalid Token !";
        }

        Calendar calendar = Calendar.getInstance();

        if (passwordToken.getExpirationTime().before(calendar.getTime())) {
            System.out.println("Verification token is Expired: " + passwordToken);
            passwordTokenRepository.delete(passwordToken);
            return "Token Expired !";
        }

        return "Valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }
    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Map<String, Object> getCurrentUserFromToken(String token) {
        // 1. Extract claims from JWT
        Map<String, Object> claims = jwtService.extractAllClaims(token);

        // 2. Fetch user from DB (optional: ensures latest data)
        String email = claims.get("email").toString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 3. Prepare response map
        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("avatarUrl", user.getAvatarUrl());
        response.put("subscriptionType", user.getSubscription().getSubscriptionType().name());
        response.put("subscriptionStatus", user.getSubscription().getStatus().name());
        response.put("createdAt", user.getCreatedAt());
        response.put("winnersSelectedThisMonth", user.getWinnersSelectedThisMonth());

        return response;
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
    public void upgradeSubscription(String paymentId) {
         //fetching payment data
         Payment payment = paymentService.getPaymentDataToUpgradeService(paymentId);

         Long userId = payment.getUserId();
         SubscriptionTypes planType = payment.getPlanType();

         Date startDate = new Date();
         Calendar cal = Calendar.getInstance();
         cal.setTime(startDate);
         cal.add(Calendar.MONTH, 1); // add 1 month
         Date expiringDate  = cal.getTime();

         SubscriptionStatus status = SubscriptionStatus.ACTIVE;

         Integer maxComments = planType.getMaxComments();
         Integer maxWinners = planType.getMaxWinners();
         Integer remainingGiveaways = planType.getMaxGiveaways();

         //building subscription
         Subscription subscription = Subscription.builder()
                                     .subscriptionType(planType)
                                     .startDate(startDate)
                                     .expiringDate(expiringDate)
                                     .status(status)
                                     .paymentId(paymentId)
                                     .maxComments(maxComments)
                                     .maxWinners(maxWinners)
                                     .remainingGiveaways(remainingGiveaways)
                                     .build();

         Optional<User> optUser = userRepository.findUserById(userId);

         if (optUser.isEmpty()) throw  new UserNotFoundException("optUser not found, unable to upgrade Subscription !");

         User user = optUser.get();
         user.setSubscription(subscription); //saving updated subscription
         userRepository.save(user); // updating the user
    }

    @Override
    public void deletePasswordToken(String token) {
        passwordTokenRepository.deleteByToken(token);
    }

}