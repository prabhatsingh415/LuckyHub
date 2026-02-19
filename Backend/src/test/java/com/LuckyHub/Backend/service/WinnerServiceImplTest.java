package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.PlanLimitExceedException;
import com.LuckyHub.Backend.exception.PlansGiveawayLimitExceedException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.exception.VideosFromDifferentChannelsException;
import com.LuckyHub.Backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WinnerServiceImplTest {

    private WinnerRequest sampleRequest;
    private String sampleEmail;
    private User sampleUser;
    private List<String> sampleVideoIds;
    private VideoMetadata sampleMetadata;
    private List<Comment> sampleFetchedComments;
    private List<Comment> sampleWinners;
    private Subscription sampleSubscription;

    @Mock
    VideoService videoService;

    @Mock
    UserService userService;

    @Mock
    GiveawayHistoryService giveawayHistoryService;

    @Mock
    SubscriptionService subscriptionService;


    @InjectMocks
    WinnerServiceImpl winnerService;


    @BeforeEach
    void setUp(){
        sampleRequest = new WinnerRequest();
        sampleRequest.setVideoLinks(
                      List.of("https://youtu.be/vXWmL1WlVPM?si=IHtTz4Be9X5p1EmH",
                              "https://youtu.be/ABdwinE12sA?si=S3XLPOpV9WD_VVMX",
                              "https://youtu.be/wdR7uTeCs7A?si=qNIpvDApyTY3zxX1"));

        sampleRequest.setKeyword("Id");
        sampleRequest.setNumberOfWinners(2);

        sampleEmail = "userMail@Test.com";

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setWinnersSelectedThisMonth(0);

        sampleSubscription = Subscription.builder()
                .id(1L)
                .subscriptionType(SubscriptionTypes.FREE)
                .remainingGiveaways(3)
                .build();

        sampleUser.setSubscription(sampleSubscription);

        sampleVideoIds = List.of("YT@123", "YT@456", "YT@789");
        sampleMetadata = VideoMetadata.builder()
                .videoIds(sampleVideoIds)
                .titles(List.of("JAVA", "C++", "Python"))
                .thumbnailUrls(List.of("URL1", "URL2", "URL3"))
                .channelIds(Collections.singleton("channel@12345"))
                .build();

        sampleFetchedComments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sampleFetchedComments.add(Comment.builder().authorName("User" + i).build());
        }
        sampleWinners = List.of(sampleFetchedComments.get(0), sampleFetchedComments.get(1));

    }

    @Test
    void findWinner_ShouldReturnWinners_WhenNothingFails() {

        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));


        when(videoService.getVideoMetadata(sampleVideoIds)).thenReturn(sampleMetadata);
        when(userService.findUserByEmail(sampleEmail)).thenReturn(Optional.ofNullable(sampleUser));


        when(videoService.fetchComments(anyList(), anyString(), any())).thenReturn(sampleFetchedComments);
        when(videoService.selectWinner(anyList(), anyInt())).thenReturn(sampleWinners);

        WinnerResponse response = winnerService.findWinner(sampleRequest, sampleEmail);

        assertNotNull(response);
        assertEquals(2, response.getWinners().size());

        verify(subscriptionService, times(1)).save(argThat(subscription ->
                subscription.getRemainingGiveaways() == 2
        ));

        verify(userService, times(1)).saveUser(argThat(user ->
                user.getWinnersSelectedThisMonth() == 2
        ));

        verify(giveawayHistoryService, times(1)).saveHistory(argThat(history ->
                history.getWinnersCount() == 2 &&
                        history.getUserId().equals(sampleUser.getId()) &&
                        history.getWinners().contains("User0")
        ));

    }

    @Test
    void findWinner_ShouldThrowException_WhenPlansGiveawayLimitExceeded(){
        sampleSubscription.setRemainingGiveaways(0);
        sampleSubscription.setSubscriptionType(SubscriptionTypes.GOLD);

        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));


        when(videoService.getVideoMetadata(sampleVideoIds)).thenReturn(sampleMetadata);
        when(userService.findUserByEmail(sampleEmail)).thenReturn(Optional.of(sampleUser));

        assertThrows(PlansGiveawayLimitExceedException.class, ()->  winnerService.findWinner(sampleRequest, sampleEmail));

        verify(videoService, never()).selectWinner(sampleFetchedComments, sampleRequest.getNumberOfWinners());
        verify(videoService, never()).fetchComments(sampleVideoIds, sampleRequest.getKeyword(), SubscriptionTypes.FREE);
        verify(subscriptionService, never()).save(any());
        verify(userService, never()).saveUser(any());
        verify(giveawayHistoryService, never()).saveHistory(any());
    }

    @Test
    void findWinner_ShouldThrowException_WhenPlanLimitExceeded(){
        sampleSubscription.setSubscriptionType(SubscriptionTypes.GOLD);
        sampleRequest.setNumberOfWinners(10);

        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));


        when(videoService.getVideoMetadata(sampleVideoIds)).thenReturn(sampleMetadata);
        when(userService.findUserByEmail(sampleEmail)).thenReturn(Optional.of(sampleUser));

         assertThrows(PlanLimitExceedException.class, ()->  winnerService.findWinner(sampleRequest, sampleEmail));

         verify(videoService, never()).selectWinner(sampleFetchedComments, sampleRequest.getNumberOfWinners());
         verify(videoService, times(0)).fetchComments(sampleVideoIds, sampleRequest.getKeyword(), SubscriptionTypes.FREE);
         verify(subscriptionService, never()).save(any());
         verify(userService, never()).saveUser(any());
         verify(giveawayHistoryService, never()).saveHistory(any());
    }

    @Test
    void findWinner_ShouldThrowException_WhenVideoUrlsAreFromDifferentChannels() {

        VideoMetadata multiChannelMetadata = VideoMetadata.builder()
                .videoIds(sampleVideoIds)
                .titles(List.of("Title 1", "Title 2", "Title 3"))
                .thumbnailUrls(List.of("Url 1", "Url 2", "Url 3"))
                .channelIds(Set.of("channel@1", "channel@2"))
                .build();

        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));

        when(videoService.getVideoMetadata(anyList())).thenReturn(multiChannelMetadata);

        assertThrows(VideosFromDifferentChannelsException.class, () ->
                winnerService.findWinner(sampleRequest, sampleEmail));

        verify(userService, never()).findUserByEmail(anyString());
        verify(videoService, never()).fetchComments(anyList(), anyString(), any());
        verify(subscriptionService, never()).save(any());
        verify(giveawayHistoryService, never()).saveHistory(any());
    }

    @Test
    void findWinner_ShouldThrowException_WhenUserNotFound(){

        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));

        when(videoService.getVideoMetadata(sampleVideoIds)).thenReturn(sampleMetadata);
        when(userService.findUserByEmail(sampleEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()->  winnerService.findWinner(sampleRequest, sampleEmail));

        verify(videoService,never()).fetchComments(sampleVideoIds, sampleRequest.getKeyword(), SubscriptionTypes.FREE);
        verify(videoService, never()).selectWinner(sampleFetchedComments, sampleRequest.getNumberOfWinners());
        verify(subscriptionService, never()).save(any());
        verify(userService, never()).saveUser(any());
        verify(giveawayHistoryService, never()).saveHistory(any());
    }


    @Test
    void findWinner_ShouldNotDecrementLimit_WhenUserIsDiamond() {
        sampleSubscription.setSubscriptionType(SubscriptionTypes.DIAMOND);
        sampleSubscription.setRemainingGiveaways(5);

        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));
        when(videoService.getVideoMetadata(anyList())).thenReturn(sampleMetadata);
        when(userService.findUserByEmail(sampleEmail)).thenReturn(Optional.of(sampleUser));
        when(videoService.fetchComments(anyList(), anyString(), any())).thenReturn(sampleFetchedComments);
        when(videoService.selectWinner(anyList(), anyInt())).thenReturn(sampleWinners);

        winnerService.findWinner(sampleRequest, sampleEmail);

        verify(subscriptionService, never()).save(any());
        verify(userService, times(1)).saveUser(argThat(u -> u.getWinnersSelectedThisMonth() == 2));
        assertEquals(5, sampleSubscription.getRemainingGiveaways());
    }

    @Test
    void findWinner_ShouldReturnEmptyResponse_WhenNoCommentsMatchKeyword() {
        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));
        when(videoService.getVideoMetadata(anyList())).thenReturn(sampleMetadata);
        when(userService.findUserByEmail(sampleEmail)).thenReturn(Optional.of(sampleUser));
        when(videoService.fetchComments(anyList(), anyString(), any())).thenReturn(sampleFetchedComments);
        when(videoService.selectWinner(anyList(), anyInt())).thenReturn(Collections.emptyList());

        WinnerResponse response = winnerService.findWinner(sampleRequest, sampleEmail);

        assertNotNull(response);
        assertTrue(response.getWinners().isEmpty());
        verify(subscriptionService, never()).save(any());
        verify(userService, never()).saveUser(any());
        verify(giveawayHistoryService, never()).saveHistory(any());
    }

    @Test
    void findWinner_ShouldSetLoyaltyTrue_WhenMultipleVideosProvided() {
        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));
        when(videoService.getVideoMetadata(anyList())).thenReturn(sampleMetadata);
        when(userService.findUserByEmail(sampleEmail)).thenReturn(Optional.of(sampleUser));
        when(videoService.fetchComments(anyList(), anyString(), any())).thenReturn(sampleFetchedComments);
        when(videoService.selectWinner(anyList(), anyInt())).thenReturn(sampleWinners);

        winnerService.findWinner(sampleRequest, sampleEmail);

        verify(giveawayHistoryService).saveHistory(argThat(GiveawayHistory::isLoyaltyFilterApplied));
    }

    @Test
    void findWinner_ShouldThrowException_WhenVideoServiceFails() {
        when(videoService.extractVideoId(anyString()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(sampleVideoIds));
        when(videoService.getVideoMetadata(anyList())).thenThrow(new RuntimeException("API Failure"));

        assertThrows(RuntimeException.class, () -> winnerService.findWinner(sampleRequest, sampleEmail));

        verify(subscriptionService, never()).save(any());
        verify(userService, never()).saveUser(any());
        verify(giveawayHistoryService, never()).saveHistory(any());
    }

}