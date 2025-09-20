package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

        private final UserRepository userRepository;

        public SubscriptionServiceImpl(UserRepository userRepository) {
            this.userRepository = userRepository;
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


    @Override
    public boolean verifyTheAmount(int subAmount) {

         for (SubscriptionTypes subscriptionTypes : SubscriptionTypes.values()){
             if(subscriptionTypes.getPrice() == subAmount)return true;
         }

        return false;
    }

    @Override
    public String getPlanByAmount(int subAmount) {
        if(SubscriptionTypes.FREE.getPrice() == subAmount){
            return "FREE";
        }else if (SubscriptionTypes.GOLD.getPrice() == subAmount){
            return "GOLD";
        }else {
            return "DIAMOND";
        }
    }
}
