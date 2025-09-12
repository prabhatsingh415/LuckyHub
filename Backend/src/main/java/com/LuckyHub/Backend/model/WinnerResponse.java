package com.LuckyHub.Backend.model;

import lombok.Data;

import java.util.List;

@Data
public class WinnerResponse {
    private List<String> winnerNames;
    private String message;
}
