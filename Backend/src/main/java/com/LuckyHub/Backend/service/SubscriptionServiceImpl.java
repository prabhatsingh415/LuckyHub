package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JWTService jwtService;

    public SubscriptionServiceImpl(UserRepository userRepository, UserService userService, JWTService jwtService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @Override
    public boolean verifyTheAmount(int subAmount) {

         for (SubscriptionTypes subscriptionTypes : SubscriptionTypes.values()){
             if(subscriptionTypes.getPrice() == subAmount)return true;
         }

        return false;
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

    public Long getUserId(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);

        String email = jwtService.extractUserEmail(token);
        Long ID = userService.findUserIdByEmail(email);

        return ID;
    }

}
