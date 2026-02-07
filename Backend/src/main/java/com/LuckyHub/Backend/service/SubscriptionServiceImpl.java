package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.InvalidAmountForAnyPlanException;
import com.LuckyHub.Backend.model.SubscriptionResponse;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final CacheManager cacheManager;
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(CacheManager cacheManager, @Lazy UserService userService, SubscriptionRepository subscriptionRepository) {
        this.cacheManager = cacheManager;
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public SubscriptionTypes getPlanByAmount(BigDecimal subAmount) {
        for (SubscriptionTypes type : SubscriptionTypes.values()) {
            if (BigDecimal.valueOf(type.getPrice()).compareTo(subAmount) == 0) {
                return type;
            }
        }
        throw new InvalidAmountForAnyPlanException("Invalid subscription amount: " + subAmount);
    }

    @Override
    public SubscriptionResponse getUserSubscription(User user) {
        Subscription sub =  user.getSubscription();

        return SubscriptionResponse.builder()
                .planName(sub.getSubscriptionType().name())
                .status(sub.getStatus().name())
                .startDate(sub.getStartDate())
                .expiryDate(sub.getExpiringDate())
                .maxComments(sub.getMaxComments())
                .maxWinners(sub.getMaxWinners())
                .remainingGiveaways(sub.getRemainingGiveaways())
                .paymentReference(sub.getPaymentId())
                .isActive(sub.getStatus() == SubscriptionStatus.ACTIVE)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "dashboardCache", key = "#user.email")
    public void upgradeSubscription(User user, Payment payment) {
        // fetching payment data
        SubscriptionTypes planType = payment.getPlanType();

        Date startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, 1); // add 1 month
        Date expiringDate  = cal.getTime();

        SubscriptionStatus status = SubscriptionStatus.ACTIVE;

        Integer maxComments = planType.getMaxComments();
        Integer maxWinners = planType.getMaxWinners();
        Integer remainingGiveaways = planType.getMaxGiveaways();


        Subscription subscription = user.getSubscription();

        if (subscription != null) {
            // update existing subscription
            subscription.setSubscriptionType(planType);
            subscription.setStartDate(startDate);
            subscription.setExpiringDate(expiringDate);
            subscription.setStatus(status);
            subscription.setPaymentId(payment.getPaymentId());
            subscription.setMaxComments(maxComments);
            subscription.setMaxWinners(maxWinners);
            subscription.setRemainingGiveaways(remainingGiveaways);
        } else {
            // create new subscription if user has none
            subscription = Subscription.builder()
                    .subscriptionType(planType)
                    .startDate(startDate)
                    .expiringDate(expiringDate)
                    .status(status)
                    .paymentId(payment.getPaymentId())
                    .maxComments(maxComments)
                    .maxWinners(maxWinners)
                    .remainingGiveaways(remainingGiveaways)
                    .user(user) // link to user
                    .build();
            user.setSubscription(subscription);
        }

        userService.saveUser(user);// updating the user with updated/new subscription
        log.info("Plan upgraded successfully for {}", user.getEmail());
    }

    @Scheduled(cron = "0 0 0 1 * ?") // monthly reset
    public void resetMonthlyQuota() {
        log.info("[Subscription-CRON] Starting monthly quota reset for all users...");

        subscriptionRepository.bulkResetAllToFree(
                SubscriptionTypes.FREE,
                SubscriptionTypes.FREE.getMaxGiveaways(),
                SubscriptionTypes.FREE.getMaxComments(),
                SubscriptionTypes.FREE.getMaxWinners()
        );

        userService.resetAllWinnersCount();
        this.evictAllDashboardCache();
        log.info("[Subscription-CRON] Monthly reset successful!");
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetExpiredSubscriptions() {
        log.info("[Subscription-CRON] Checking for expired subscriptions...");

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.MONTH, 1); // add 1 mont
        Date expiringDate  = cal.getTime();

        subscriptionRepository.bulkResetExpired(
                SubscriptionTypes.FREE,
                SubscriptionTypes.FREE.getMaxGiveaways(),
                SubscriptionTypes.FREE.getMaxComments(),
                SubscriptionTypes.FREE.getMaxWinners(),
   expiringDate
          );

        this.evictAllDashboardCache();
        log.info("[Subscription-CRON] Expiry check completed!");
    }

    private void evictAllDashboardCache() {
        if (cacheManager.getCache("dashboardCache") != null) {
            cacheManager.getCache("dashboardCache").clear();
            log.info("[Subscription-CACHE] dashboardCache has been cleared.");
        }
    }
}
