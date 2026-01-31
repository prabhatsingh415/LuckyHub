package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.repository.GiveawayHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiveawayHistoryServiceImpl implements GiveawayHistoryService{

    private final GiveawayHistoryRepository giveawayHistoryRepository;

    public GiveawayHistoryServiceImpl(GiveawayHistoryRepository giveawayHistoryRepository) {
        this.giveawayHistoryRepository = giveawayHistoryRepository;
    }


    @Override
    public void saveHistory(GiveawayHistory giveawayHistory) {
        giveawayHistoryRepository.save(giveawayHistory);

        List<GiveawayHistory> userHistory =
                giveawayHistoryRepository.findByUserIdOrderByCreatedAtAsc(giveawayHistory.getUserId());

        // Clean Up
        if (userHistory.size() > 5) {
            int extra = userHistory.size() - 5;
            for (int i = 0; i < extra; i++) {
                giveawayHistoryRepository.delete(userHistory.get(i)); // delete oldest
            }
        }
    }

    @Override
    public List<GiveawayHistory> history(long userId) {
        return giveawayHistoryRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteHistory(long userId ) {
        giveawayHistoryRepository.deleteByUserId(userId);
    }
}
