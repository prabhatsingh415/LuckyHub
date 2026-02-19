package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.model.GiveawayHistoryDTO;
import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;
import com.LuckyHub.Backend.service.GiveawayHistoryService;
import com.LuckyHub.Backend.service.UserService;
import com.LuckyHub.Backend.service.WinnerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/giveaway")
@AllArgsConstructor
@Slf4j
public class GiveawayController {

    private final WinnerService winnerService;
    private final GiveawayHistoryService giveawayHistoryService;
    private final UserService userService;


    @PostMapping("/pick-a-winner")
    public ResponseEntity<WinnerResponse> getWinner(@RequestBody WinnerRequest request, @AuthenticationPrincipal UserDetails userDetails){
        log.info("Winner selection request received from: {}", userDetails.getUsername());
        String email = userDetails.getUsername();
        WinnerResponse winners = winnerService.findWinner(request, email);
        return ResponseEntity.ok(winners);
    }

    @GetMapping("/history")
    public ResponseEntity<List<GiveawayHistoryDTO>> getHistory(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        log.info("Fetching giveaway history for: {}", email);
        Long userId = userService.findUserIdByEmail(email);

        GiveawayHistoryDTO[] historyArray = giveawayHistoryService.history(userId);

        return ResponseEntity.ok(List.of(historyArray));
    }

}
