package com.LuckyHub.Backend.service;

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
    private final JWTService jwtService;

    public GoogleAuthService(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Process Google user info map and return JWT
    public String processUser(Map<String, Object> userInfo) {
        String email = userInfo.get("email").toString();
        String firstName = userInfo.getOrDefault("given_name", "").toString();
        String lastName = userInfo.getOrDefault("family_name", "").toString();
        String avatarURL = userInfo.getOrDefault(
                "picture",
                "https://cdn-icons-png.flaticon.com/512/149/149071.png" // default avatar
        ).toString();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    //Adding Subscription
                    Subscription subscription = new Subscription();
                    subscription.setSubscriptionType(SubscriptionTypes.FREE);
                    subscription.setStatus(SubscriptionStatus.NONE);
                    subscription.setStartDate(null);
                    subscription.setExpiringDate(null);
                    subscription.setPaymentId(null);

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
        return jwtService.generateToken(user);
    }
}
