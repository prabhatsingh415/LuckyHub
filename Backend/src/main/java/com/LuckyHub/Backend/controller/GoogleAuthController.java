package com.LuckyHub.Backend.controller;


import com.LuckyHub.Backend.service.GoogleAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;


@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {


    private final GoogleAuthService googleAuthService;

    @Value("${app_frontend_auth-success-url}")
    private String frontendSuccessUrl;

    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String code){
        log.info("oauth Request reached !");
        ResponseCookie refreshCookie =  googleAuthService.authenticateGoogleUser(code);

        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.set(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        resHeaders.setLocation(URI.create(frontendSuccessUrl));
        return new ResponseEntity<>(resHeaders, HttpStatus.FOUND);
    }
}