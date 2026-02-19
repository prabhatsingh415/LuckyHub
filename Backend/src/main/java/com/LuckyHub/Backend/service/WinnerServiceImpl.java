package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VideoDetail;
import com.LuckyHub.Backend.exception.*;
import com.LuckyHub.Backend.model.*;
import com.LuckyHub.Backend.repository.SubscriptionRepository;
import com.LuckyHub.Backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class WinnerServiceImpl implements WinnerService {

    private final VideoService videoService;
    private final UserService userService;
    private final GiveawayHistoryService giveawayHistoryService;
    private final SubscriptionService subscriptionService;

    public WinnerServiceImpl(VideoService videoService, UserService userService, GiveawayHistoryService giveawayHistoryService, SubscriptionService subscriptionService) {
        this.videoService = videoService;
        this.userService = userService;
        this.giveawayHistoryService = giveawayHistoryService;
        this.subscriptionService = subscriptionService;
    }

    @Transactional
    @Override
    @CacheEvict(value = "dashboardCache", key = "#email")
    public WinnerResponse findWinner(WinnerRequest request, String email) {
        log.info("find a winner request reached !");

        List<String> videoIds = request.getVideoLinks().stream()
                .map(videoService::extractVideoId).toList();

        if (videoIds.isEmpty()) {
            throw new VideoIdNotFoundExeption("Video ids not found !");
        }

        VideoMetadata metadata = videoService.getVideoMetadata(videoIds);

        if (metadata.getChannelIds().size() > 1) {
            throw new VideosFromDifferentChannelsException("All videos must be from the same channel.");
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

        List<Comment> fetchedComments = videoService.fetchComments(videoIds, request.getKeyword(), subscription.getSubscriptionType());
        List<Comment> eligibleComments = fetchedComments;

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            eligibleComments = fetchedComments.stream()
                    .filter(Comment::isContainsKeyword)
                    .toList();
        }

        List<Comment> winners = videoService.selectWinner(
                eligibleComments,
                request.getNumberOfWinners()
        );

        List<VideoDetail> videoDetails = IntStream.range(0, metadata.getVideoIds().size())
                .mapToObj(i -> new VideoDetail(
                        metadata.getVideoIds().get(i),
                        metadata.getThumbnailUrls().get(i),
                        metadata.getTitles().get(i)
                ))
                .toList();

        if (winners.isEmpty()) {
            log.info("fetched Comments {}", fetchedComments.size());
            log.info("No winners found !");
            return new WinnerResponse(winners);
        }

        if (subscription.getSubscriptionType() != SubscriptionTypes.DIAMOND) {
            subscription.setRemainingGiveaways(subscription.getRemainingGiveaways() - 1);
            subscriptionService.save(subscription);
        }

        user.setWinnersSelectedThisMonth(user.getWinnersSelectedThisMonth() + winners.size());
        userService.saveUser(user);

        GiveawayHistory history = GiveawayHistory.builder()
                .userId(user.getId())
                .commentCount(fetchedComments.size())
                .winnersCount(winners.size())
                .winners(winners.stream().map(Comment::getAuthorName).toList())
                .videoDetails(videoDetails)
                .keywordUsed(request.getKeyword())
                .loyaltyFilterApplied(videoIds.size() > 1)
                .createdAt(LocalDateTime.now())
                .build();

        giveawayHistoryService.saveHistory(history);

        return new WinnerResponse(winners);
    }
}
