package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.GoogleAuthenticationFailedException;
import com.LuckyHub.Backend.exception.UnauthorizedException;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.utils.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final RefreshTokenService refreshTokenService;
    private final RestTemplate restTemplate;
    private final UserService userService;

    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String redirectURL;

    @Override
    public ResponseCookie authenticateGoogleUser(String code) {
        try{
            RefreshToken refreshToken = this.processUser(code); // Token
            return RefreshTokenUtil.buildRefreshCookie(refreshToken.getToken());
        }catch (Exception e){
            log.error("Authentication error: {}", e.getMessage());
            throw new GoogleAuthenticationFailedException("OAuth request failed!");
        }
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
            throw new UnauthorizedException("Google Auth Failed");
        }
            Map userInfo = userInfoResponse.getBody();

            String email = userInfo.get("email").toString();
            String firstName = userInfo.getOrDefault("given_name", "").toString();
            String lastName = userInfo.getOrDefault("family_name", "").toString();

            User user =
                     userService.findUserByEmail(email)
                    .orElseGet(() -> {
                        //Adding Subscription
                        UserModel model = new UserModel();
                        model.setEmail(email);
                        model.setFirstName(firstName);
                        model.setLastName(lastName);
                        model.setPassword(UUID.randomUUID().toString());

                        return userService.save(model, true);
                    });

            return refreshTokenService.createRefreshToken(user);
    }

}
