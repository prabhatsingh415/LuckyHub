package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.PlanLimitExceedException;
import com.LuckyHub.Backend.exception.PlansGiveawayLimitExceedException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.exception.VideosFromDifferentChannelsException;
import com.LuckyHub.Backend.model.Comment;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;
import com.LuckyHub.Backend.repository.GiveawayHistoryRepository;
import com.LuckyHub.Backend.repository.SubscriptonRepository;
import com.LuckyHub.Backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class WinnerServiceImpl implements WinnerService {

    private final VideoService videoService;
    private final UserService userService;
    private final SubscriptonRepository subscriptionRepository;
    private final GiveawayHistoryService giveawayHistoryService;
    private final CacheManager cacheManager;
    private final UserRepository userRepository;

    public WinnerServiceImpl(VideoService videoService, UserService userService, SubscriptonRepository subscriptonRepository, GiveawayHistoryRepository giveawayHistoryRepository, GiveawayHistoryService giveawayHistoryService, CacheManager cacheManager, UserRepository userRepository) {
        this.videoService = videoService;
        this.userService = userService;
        this.subscriptionRepository = subscriptonRepository;
        this.giveawayHistoryService = giveawayHistoryService;
        this.cacheManager = cacheManager;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public WinnerResponse findWinner(WinnerRequest request, String email) {

        if (!videoService.verifySameUser(request.getVideoLinks())) {
            throw new VideosFromDifferentChannelsException("All provided videos must be from the same channel.");
        }

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found, please try login!"));

        Subscription subscription = user.getSubscription();

        if (subscription.getSubscriptionType() != SubscriptionTypes.DIAMOND) {
            if (subscription.getRemainingGiveaways() <= 0) {
                throw new PlansGiveawayLimitExceedException("You have reached your monthly limit!");
            }
        }

        if (subscription.getSubscriptionType().getMaxWinners() < request.getNumberOfWinners()) {
            throw new PlanLimitExceedException("Winners exceed your plan limit.");
        }

        List<Comment> fetchedComments = videoService.fetchComments(request.getVideoLinks(), request.getKeyword(), subscription.getSubscriptionType());
        List<Comment> winners = videoService.selectWinner(fetchedComments, request.getNumberOfWinners());

        if (winners.isEmpty()) {
            return new WinnerResponse(winners);
        }

        if (subscription.getSubscriptionType() != SubscriptionTypes.DIAMOND) {
            subscription.setRemainingGiveaways(subscription.getRemainingGiveaways() - 1);
            subscriptionRepository.save(subscription);
        }

        user.setWinnersSelectedThisMonth(user.getWinnersSelectedThisMonth() + 1);
        userRepository.save(user);

        List<String> winnerNames = winners.stream().map(Comment::getAuthorName).toList();

        GiveawayHistory giveawayHistory = GiveawayHistory.builder()
                .userId(user.getId())
                .commentCount(fetchedComments.size())
                .winnersCount(winners.size())
                .winners(winnerNames)
                .createdAt(LocalDateTime.now())
                .build();

        giveawayHistoryService.saveHistory(giveawayHistory);

        if (cacheManager.getCache("dashboardCache") != null) {
            Objects.requireNonNull(cacheManager.getCache("dashboardCache")).evict(email);
        }
        return new WinnerResponse(winners);
    }
}
