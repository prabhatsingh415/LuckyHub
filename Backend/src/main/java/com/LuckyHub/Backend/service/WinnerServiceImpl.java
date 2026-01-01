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

    public WinnerServiceImpl(VideoService videoService, UserService userService, SubscriptonRepository subscriptonRepository, GiveawayHistoryRepository giveawayHistoryRepository, GiveawayHistoryService giveawayHistoryService, CacheManager cacheManager) {
        this.videoService = videoService;
        this.userService = userService;
        this.subscriptionRepository = subscriptonRepository;
        this.giveawayHistoryService = giveawayHistoryService;
        this.cacheManager = cacheManager;
    }

    @Override
    public WinnerResponse findWinner(WinnerRequest request, String email) {

         if(!videoService.verifySameUser(request.getVideoLinks())) // verify all url are of same YOUTUBE channel
              throw new VideosFromDifferentChannelsException("All provided videos must be from the same channel.");

        Optional<User> user = userService.findUserByEmail(email);   //find the user to get plan details

        if(user.isEmpty())throw new UserNotFoundException("user not found, please try login !");

        Subscription subscription = user.get().getSubscription();
        SubscriptionTypes plan = subscription.getSubscriptionType(); //get plan

        //Check the request is within the limit or not ?
        if(plan.getMaxWinners() < request.getNumberOfWinners()){
            throw new PlanLimitExceedException("Number of winners requested exceeds the limit for your subscription plan.");
        }

        if (subscription.getSubscriptionType() != SubscriptionTypes.DIAMOND) {
            if (subscription.getRemainingGiveaways() <= 0) {
                throw new PlansGiveawayLimitExceedException("You have reached your monthly giveaway limit. Upgrade to get more!");
            }
            subscription.setRemainingGiveaways(subscription.getRemainingGiveaways() - 1);
            subscriptionRepository.save(subscription);
        }


        //fetch the comments
        List<Comment> fetchedComments = videoService.fetchComments(request.getVideoLinks(), request.getKeyword(), plan);

        List<Comment> winners = videoService.selectWinner(fetchedComments,request.getNumberOfWinners());

        List<String> winnerNames = winners.stream()
                .map(Comment::getAuthorName)
                .toList();


        GiveawayHistory giveawayHistory = GiveawayHistory
                .builder()
                .commentCount(fetchedComments.size())
                .winnersCount(winners.size())
                .winners(winnerNames)
                .createdAt(LocalDateTime.now())
                .build();

        giveawayHistoryService.saveHistory(giveawayHistory);

        if (cacheManager.getCache("dashboardCache") != null) {
            Objects.requireNonNull(cacheManager.getCache("dashboardCache")).evict(email);
        }

        return new WinnerResponse(
                winners
        );
    }
}
