package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;

public interface WinnerService {
    WinnerResponse findWinner(WinnerRequest request);
}
