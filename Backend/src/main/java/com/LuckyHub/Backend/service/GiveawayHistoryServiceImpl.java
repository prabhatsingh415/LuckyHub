package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.model.GiveawayHistoryDTO;
import com.LuckyHub.Backend.model.VideoDetailDTO;
import com.LuckyHub.Backend.repository.GiveawayHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GiveawayHistoryServiceImpl implements GiveawayHistoryService{

    private final GiveawayHistoryRepository giveawayHistoryRepository;

    @Override
    @Transactional
    @CacheEvict(value = "historyCache", key = "#giveawayHistory.userId")
    public void saveHistory(GiveawayHistory giveawayHistory) {
        log.info("[History] Processing history for user: {}", giveawayHistory.getUserId());
        giveawayHistoryRepository.save(giveawayHistory);

        List<GiveawayHistory> userHistory =
                giveawayHistoryRepository.findByUserIdOrderByCreatedAtDesc(giveawayHistory.getUserId());

        // Clean Up
        int LIMIT = 10;
        if (userHistory.size() > LIMIT) {
            log.info("[History] Pruning old records for user: {}", giveawayHistory.getUserId());

            List<GiveawayHistory> toDelete = userHistory.subList(LIMIT, userHistory.size());

            giveawayHistoryRepository.deleteAllInBatch(toDelete);
        }
    }

    @Override
    @Cacheable(value = "historyCache", key = "#userId")
    @Transactional
    public GiveawayHistoryDTO[] history(long userId) {

        List<GiveawayHistory> entities = giveawayHistoryRepository.findByUserId(userId);

        List<GiveawayHistoryDTO> list = entities.stream().map(entity ->
                GiveawayHistoryDTO.builder()
                        .id(entity.getId())
                        .userId(entity.getUserId())
                        .winners(entity.getWinners() != null ? new ArrayList<>(entity.getWinners()) : null)
                        .winnersCount(entity.getWinnersCount())
                        .commentCount(entity.getCommentCount())
                        .keywordUsed(entity.getKeywordUsed())
                        .loyaltyFilterApplied(entity.isLoyaltyFilterApplied())
                        .createdAt(entity.getCreatedAt())
                        .videoDetails(entity.getVideoDetails() != null ?
                                entity.getVideoDetails().stream()
                                        .map(v -> new VideoDetailDTO(v.getVideoId(), v.getThumbnail(), v.getTitle()))
                                        .toList()
                                : null)
                        .build()
        ).toList();

        return list.toArray(new GiveawayHistoryDTO[0]);
    }

    @Transactional
    @Override
    public void deleteHistory(long userId ) {
        giveawayHistoryRepository.deleteByUserId(userId);
    }
}
