package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.model.GiveawayHistoryDTO;

import java.util.List;

public interface GiveawayHistoryService {
    void saveHistory(GiveawayHistory giveawayHistory);

    GiveawayHistoryDTO[] history(long userId);

    void deleteHistory(long userId);
}
