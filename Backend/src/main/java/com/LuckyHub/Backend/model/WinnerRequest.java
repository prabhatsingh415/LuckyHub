package com.LuckyHub.Backend.model;

import lombok.Data;

import java.util.List;

@Data
public class WinnerRequest{
    private List<String> videoLinks; // 1–5 YouTube links
    private String keyword;          // optional
    private int numberOfWinners;
    private Long giveawayId;
}
