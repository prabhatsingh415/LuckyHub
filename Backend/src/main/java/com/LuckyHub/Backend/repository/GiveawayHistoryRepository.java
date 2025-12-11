package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiveawayHistoryRepository extends JpaRepository<GiveawayHistory, Long> {
    List<GiveawayHistory> findByUserIdOrderByCreatedAtAsc(Long userId);
    List<GiveawayHistory> findByUserId(Long userId);

}
