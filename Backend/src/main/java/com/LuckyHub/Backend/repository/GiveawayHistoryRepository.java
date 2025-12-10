package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiveawayHistoryRepository extends JpaRepository<GiveawayHistory, Long> {
}
