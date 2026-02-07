package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // quota reset query
    @Modifying
    @Transactional
    @Query("UPDATE Subscription s SET s.subscriptionType = :freeType, " +
            "s.remainingGiveaways = :giveaways, s.maxComments = :comments, s.maxWinners = :winners")
    void bulkResetAllToFree(@Param("freeType") SubscriptionTypes freeType,
                            @Param("giveaways") Integer giveaways,
                            @Param("comments") Integer comments,
                            @Param("winners") Integer winners);

    // expired reset query
    @Modifying
    @Transactional
    @Query("UPDATE Subscription s SET s.subscriptionType = :freeType, " +
            "s.remainingGiveaways = :giveaways, s.maxComments = :comments, s.maxWinners = :winners " +
            "WHERE s.subscriptionType != :freeType AND s.expiringDate < :today")
    void bulkResetExpired(@Param("freeType") SubscriptionTypes freeType,
                          @Param("giveaways") Integer giveaways,
                          @Param("comments") Integer comments,
                          @Param("winners") Integer winners,
                          @Param("today") Date today);
}
