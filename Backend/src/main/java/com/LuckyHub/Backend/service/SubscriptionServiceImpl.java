package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl {

        private final UserRepository userRepository;

        public SubscriptionServiceImpl(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Scheduled(cron = "0 0 0 1 * ?")
        public void resetMonthlyQuota() {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                switch (user.getSubscription().getSubscriptionType()) {
                    case FREE -> user.setRemainingWinners(SubscriptionTypes.FREE.getMaxWinners());
                    case GOLD -> user.setRemainingWinners(SubscriptionTypes.GOLD.getMaxWinners());
                    case DIAMOND -> user.setRemainingWinners(SubscriptionTypes.DIAMOND.getMaxWinners());
                }
            }
            userRepository.saveAll(users);
        }
}
