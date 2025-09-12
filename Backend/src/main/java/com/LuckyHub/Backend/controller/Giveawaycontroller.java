package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;
import com.LuckyHub.Backend.service.WinnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/giveaway")
public class Giveawaycontroller {

    private final WinnerService winnerService;

    public Giveawaycontroller(WinnerService winnerService) {
        this.winnerService = winnerService;
    }

    public ResponseEntity<?> getWinner(@RequestBody WinnerRequest request){
        WinnerResponse winners = winnerService.findWinner(request);

        return ResponseEntity.ok().body(winners);
    }
}
