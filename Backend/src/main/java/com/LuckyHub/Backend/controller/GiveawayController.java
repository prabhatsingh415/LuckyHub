package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.exception.JWTTokenNotFoundOrInvalidException;
import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;
import com.LuckyHub.Backend.service.GiveawayHistoryService;
import com.LuckyHub.Backend.service.JWTService;
import com.LuckyHub.Backend.service.UserService;
import com.LuckyHub.Backend.service.WinnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/giveaway")
public class GiveawayController {

    private final WinnerService winnerService;
    private final GiveawayHistoryService giveawayHistoryService;
    private final UserService userService;

    public GiveawayController(WinnerService winnerService, GiveawayHistoryService giveawayHistoryService, UserService userService) {
        this.winnerService = winnerService;

        this.giveawayHistoryService = giveawayHistoryService;
        this.userService = userService;
    }

    @PostMapping("/pick-a-winner")
    public ResponseEntity<WinnerResponse> getWinner(@RequestBody WinnerRequest request, @AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        WinnerResponse winners = winnerService.findWinner(request, email);
        return ResponseEntity.ok().body(winners);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        Long userId = userService.findUserIdByEmail(email);
        List<GiveawayHistory> history = giveawayHistoryService.history(userId);

        return  ResponseEntity.ok().body(
                Map.of(
                        "Success", 200,
                        "history", history
                )
        );
    }
}
