package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    public SubscriptionServiceImpl(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public SubscriptionTypes getPlanByAmount(int subAmount) {
        for (SubscriptionTypes type : SubscriptionTypes.values()) {
            if (type.getPrice() == subAmount) return type;
        }
        throw new IllegalArgumentException("Invalid subscription amount: " + subAmount);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyQuota() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.getSubscription().setSubscriptionType(SubscriptionTypes.FREE);
            user.getSubscription().setRemainingGiveaways(SubscriptionTypes.FREE.getMaxGiveaways());
        }
        userRepository.saveAll(users);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetExpiredSubscriptions() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();
        for (User user : users) {
            Subscription sub = user.getSubscription();
            if (sub.getSubscriptionType() != SubscriptionTypes.FREE
                    && sub.getExpiringDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(today.plusDays(1))) {

                sub.setSubscriptionType(SubscriptionTypes.FREE);
                sub.setRemainingGiveaways(SubscriptionTypes.FREE.getMaxGiveaways());
                sub.setMaxComments(SubscriptionTypes.FREE.getMaxComments());
                sub.setMaxWinners(SubscriptionTypes.FREE.getMaxWinners());
            }
        }
        userRepository.saveAll(users);
    }

    @Override
    public Object getUserSubscription(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            return new UserNotFoundException("User not found !");
        }
        return user.get().getSubscription();
    }

    @Override
    @Transactional
    public void upgradeSubscription(Payment payment) {
        // fetching payment data
        Long userId = payment.getUserId();
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

        Optional<User> optUser = userRepository.findUserById(userId);

        if (optUser.isEmpty())
            throw new UserNotFoundException("User not found!");

        User user = optUser.get();
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

        userRepository.save(user); // updating the user with updated/new subscription
        log.info("Plan upgraded successfully for {}", user.getEmail());

        if (cacheManager.getCache("dashboardCache") != null) {
            Objects.requireNonNull(cacheManager.getCache("dashboardCache")).evict(user.getEmail());
        }
    }
}
