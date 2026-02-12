package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.SubscriptionResponse;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private UserService userService;
    @Mock private CacheManager cacheManager;

    @Mock
    private org.springframework.cache.Cache dashboardCache;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User sampleUser;
    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setEmail("test@luckyhub.com");
        sampleUser.setSubscription(Subscription.builder()
                  .subscriptionType(SubscriptionTypes.FREE)
                  .status(SubscriptionStatus.ACTIVE)
                  .remainingGiveaways(3)
                  .build());

        samplePayment = new Payment();
        samplePayment.setPaymentId("pay_abc123");
        samplePayment.setPlanType(SubscriptionTypes.GOLD);
    }

    // Verifies BigDecimal to Enum mapping (49.00 -> GOLD)
    @Test
    void getPlanByAmount_ShouldReturnCorrectType() {
        SubscriptionTypes type = subscriptionService.getPlanByAmount(new BigDecimal("49.00"));
        assertEquals(SubscriptionTypes.GOLD, type);
    }

    // Verifies mapping of Entity to Response Model
    @Test
    void getUserSubscription_ShouldMapCorrectly() {
        SubscriptionResponse response = subscriptionService.getUserSubscription(sampleUser);

        assertEquals("FREE", response.getPlanName());
        assertTrue(response.isActive());
        assertEquals(SubscriptionStatus.ACTIVE.name(), response.getStatus());
    }

    // Verifies Date calculation and Limit Reset during upgrade
    @Test
    void upgradeSubscription_ShouldUpdateExistingSubscription() {
        subscriptionService.upgradeSubscription(sampleUser, samplePayment);

        Subscription sub = sampleUser.getSubscription();

        assertEquals(SubscriptionTypes.GOLD, sub.getSubscriptionType());
        assertEquals("pay_abc123", sub.getPaymentId());

        assertTrue(sub.getExpiringDate().after(sub.getStartDate()));

        long diffInMs = sub.getExpiringDate().getTime() - sub.getStartDate().getTime();
        long twentyEightDaysInMs = 28L * 24 * 60 * 60 * 1000;
        long thirtyOneDaysInMs = 31L * 24 * 60 * 60 * 1000;

        assertTrue(diffInMs >= twentyEightDaysInMs && diffInMs <= thirtyOneDaysInMs);

        verify(userService).saveUser(sampleUser);
    }

    // Verifies behavior when a brand-new user (no sub) buys a plan
    @Test
    void upgradeSubscription_ShouldCreateNew_WhenUserHasNoSub() {
        sampleUser.setSubscription(null);

        subscriptionService.upgradeSubscription(sampleUser, samplePayment);

        assertNotNull(sampleUser.getSubscription());
        assertEquals(SubscriptionTypes.GOLD, sampleUser.getSubscription().getSubscriptionType());
        verify(userService).saveUser(sampleUser);
    }

    // CRON Test: Verifies Bulk DB Update and Cache Clearing
    @Test
    void resetMonthlyQuota_ShouldInvokeRepoAndClearCache() {
        when(cacheManager.getCache("dashboardCache")).thenReturn(dashboardCache);

        subscriptionService.resetMonthlyQuota();

        verify(subscriptionRepository).bulkResetAllToFree(any(), anyInt(), anyInt(), anyInt());
        verify(userService).resetAllWinnersCount();
        verify(dashboardCache).clear();
    }

    // CRON Test: Verifies Expiry Check logic
    @Test
    void resetExpiredSubscriptions_ShouldInvokeBulkReset() {
        when(cacheManager.getCache("dashboardCache")).thenReturn(dashboardCache);

        subscriptionService.resetExpiredSubscriptions();

        verify(subscriptionRepository).bulkResetExpired(any(), anyInt(), anyInt(), anyInt(), any(Date.class));
        verify(dashboardCache).clear();
    }
}