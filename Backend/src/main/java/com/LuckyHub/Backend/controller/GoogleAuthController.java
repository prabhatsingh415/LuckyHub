package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.service.GoogleAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {

    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String redirectURL;

    private final RestTemplate restTemplate;
    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(RestTemplate restTemplate, GoogleAuthService googleAuthService) {
        this.restTemplate = restTemplate;
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String code){
        log.info(" oauth Request reached !");
        try{
           String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectURL );
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> tokenResponse =
                    restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            //ID token
            String idToken = (String) tokenResponse.getBody().get("id_token");

            //fetching User Info
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);


            if (userInfoResponse.getStatusCode() == HttpStatus.OK && userInfoResponse.getBody() != null) {
                Map userInfo = userInfoResponse.getBody();

                RefreshToken refreshToken = googleAuthService.processUser(userInfo); // Tokens

                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(14 * 24 * 60 * 60)
                        .sameSite("Strict")
                        .build();

                HttpHeaders resHeaders = new HttpHeaders();
                resHeaders.set(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                resHeaders.setLocation(URI.create("http://localhost:5173/auth-success"));
                return new ResponseEntity<>(resHeaders, HttpStatus.FOUND);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User info not found or empty response");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}