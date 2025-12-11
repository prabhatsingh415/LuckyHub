package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;

import java.util.List;

public interface GiveawayHistoryService {
    void saveHistory(GiveawayHistory giveawayHistory);

    List<GiveawayHistory> history(long userId);
}
