package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.exception.JWTTokenNotFoundOrInvalidException;
import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;
import com.LuckyHub.Backend.service.JWTService;
import com.LuckyHub.Backend.service.WinnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/giveaway")
public class GiveawayController {

    private final WinnerService winnerService;
    private final JWTService jwtService;

    public GiveawayController(WinnerService winnerService, JWTService jwtService) {
        this.winnerService = winnerService;
        this.jwtService = jwtService;
    }

    @PostMapping("/pick-a-winner")
    public ResponseEntity<WinnerResponse> getWinner(@RequestBody WinnerRequest request,
                                       @RequestHeader("Authorization") String authHeader){

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JWTTokenNotFoundOrInvalidException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUserEmail(token);
        WinnerResponse winners = winnerService.findWinner(request, email);

        return ResponseEntity.ok().body(winners);
    }
}
