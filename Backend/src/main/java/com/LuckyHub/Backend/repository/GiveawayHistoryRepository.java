package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiveawayHistoryRepository extends JpaRepository<GiveawayHistory, Long> {
    List<GiveawayHistory> findByUserIdOrderByCreatedAtAsc(Long userId);
    List<GiveawayHistory> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GiveawayHistory g WHERE g.userId = :userId")
    void deleteByUserId(Long userId);
}
