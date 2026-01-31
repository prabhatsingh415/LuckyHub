package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public GoogleAuthService(UserRepository userRepository,
                             PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    // Process Google user info map and return JWT
    public RefreshToken processUser(Map<String, Object> userInfo) {
        String email = userInfo.get("email").toString();
        String firstName = userInfo.getOrDefault("given_name", "").toString();
        String lastName = userInfo.getOrDefault("family_name", "").toString();
        String avatarURL = userInfo.getOrDefault(
                "picture",
                "https://cdn-icons-png.flaticon.com/512/4712/4712109.png" // default avatar
        ).toString();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    //Adding Subscription
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


                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    newUser.setVerified(true);
                    newUser.setCreatedAt(new Date());
                    newUser.setUpdatedAt(new Date());
                    newUser.setSubscription(subscription);
                    newUser.setAvatarUrl(avatarURL);
                    newUser.setWinnersSelectedThisMonth(0);

                    subscription.setUser(newUser);
                    return userRepository.save(newUser);
                });

        // Generate JWT token
       return refreshTokenService.createRefreshToken(user.getEmail());
    }
}
