package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.UnauthorizedException;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String redirectURL;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final RestTemplate restTemplate;


    public GoogleAuthServiceImpl(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.restTemplate = restTemplate;
    }


    // Process Google user info map and return refresh token
    @Transactional
    @Override
    public RefreshToken processUser(String code) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectURL);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> tokenResponse =
                restTemplate.postForEntity(tokenEndpoint, request, Map.class);


        //ID token
        String idToken = (String) Objects.requireNonNull(tokenResponse.getBody()).get("id_token");

        //fetching User Info
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);


        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            throw new UnauthorizedException("Google Auth Failed"); // Null mat bhej
        }
            Map userInfo = userInfoResponse.getBody();

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

            return refreshTokenService.createRefreshToken(user.getEmail());
    }
}
