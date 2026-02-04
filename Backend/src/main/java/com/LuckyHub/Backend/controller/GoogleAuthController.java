package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.service.GoogleAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;


@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {


    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String code){
        log.info(" oauth Request reached !");
        try{
              RefreshToken refreshToken = googleAuthService.processUser(code); // Tokens

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

        }catch (Exception e){
            log.error("Authentication error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}