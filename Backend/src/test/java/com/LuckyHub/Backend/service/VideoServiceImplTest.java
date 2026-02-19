package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.Comment;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.model.VideoMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VideoServiceImpl videoService;

    private List<String> sampleVideoIds;

    @BeforeEach
    void setUp() {
        String apiKey = "test-api-key";
        ReflectionTestUtils.setField(videoService, "apiKey", apiKey);
        sampleVideoIds = List.of("vid1", "vid2");
    }

    private String createMockYoutubeJson(String author, String message, String time, String nextToken) {
        String uniqueCommentId = UUID.randomUUID().toString();
        // ✨ FIX: authorChannelUrl ko dynamic banaya taaki users merge na hon
        return String.format("""
            {
              "nextPageToken": %s,
              "items": [
                {
                  "snippet": {
                    "videoId": "vid1",
                    "topLevelComment": {
                      "id": "comment_%s",
                      "snippet": {
                        "textDisplay": "%s",
                        "textOriginal": "%s",
                        "authorDisplayName": "%s",
                        "authorProfileImageUrl": "url",
                        "authorChannelUrl": "https://www.youtube.com/channel/%s_id",
                        "authorChannelId": { "value": "%s_id" },
                        "publishedAt": "%s"
                      }
                    }
                  }
                }
              ]
            }
            """,
                nextToken == null ? "null" : "\"" + nextToken + "\"",
                uniqueCommentId, message, message, author, author, author, time);
    }

    @Test
    void fetchComments_ShouldMergeAuthorsAndSortCorrectly() {
        String ownerJson = "{ \"items\": [{ \"snippet\": { \"channelId\": \"owner_ch_123\" } }] }";
        when(restTemplate.getForObject(contains("videos"), eq(String.class))).thenReturn(ownerJson);

        String json1 = createMockYoutubeJson("UserA", "Hello Id", "2023-10-01T10:00:00Z", "token123");
        String json2 = createMockYoutubeJson("UserA", "Nice Id", "2023-10-01T11:00:00Z", null);
        String json3 = createMockYoutubeJson("UserB", "Cool Id", "2023-10-01T09:00:00Z", null);

        when(restTemplate.getForObject(contains("commentThreads"), any())).thenReturn(json1, json3, json2);

        List<Comment> result = videoService.fetchComments(sampleVideoIds, "Id", SubscriptionTypes.GOLD);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("UserA", result.get(0).getAuthorName());
    }

    @Test
    void fetchComments_ShouldHandlePagination() {
        String ownerJson = "{ \"items\": [{ \"snippet\": { \"channelId\": \"owner_ch_123\" } }] }";
        when(restTemplate.getForObject(contains("videos"), any())).thenReturn(ownerJson);

        String jsonPage1 = createMockYoutubeJson("User1", "Hi Id", "2023-10-01T10:00:00Z", "token_123");
        String jsonPage2 = createMockYoutubeJson("User2", "Hello Id", "2023-10-01T11:00:00Z", null);

        when(restTemplate.getForObject(contains("commentThreads"), any()))
                .thenReturn(jsonPage1)
                .thenReturn(jsonPage2);

        List<Comment> result = videoService.fetchComments(List.of("vid1"), "Id", SubscriptionTypes.GOLD);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void extractVideoId_ShouldHandleVariousFormats() {
        assertEquals("iMd5EJVe6MA", videoService.extractVideoId("https://youtu.be/iMd5EJVe6MA?si=KBxYKAsPDwFhAN9M"));
        assertEquals("_ijaEtNzZgw", videoService.extractVideoId("https://youtu.be/_ijaEtNzZgw?si=lJQP6426MdUdPmvp"));
        assertEquals("JwvJvH2GNn8", videoService.extractVideoId("https://youtu.be/JwvJvH2GNn8?si=Gt92ioGglLSXTlBP"));
    }

    @Test
    void getVideoMetadata_ShouldReturnCorrectData() {
        String mockMetaJson = "{ \"items\": [{ \"snippet\": { \"title\": \"Java Tutorial\", \"channelId\": \"ch123\", \"thumbnails\": { \"medium\": { \"url\": \"thumb_url\" } } } }] }";
        when(restTemplate.getForObject(contains("videos"), eq(String.class))).thenReturn(mockMetaJson);

        VideoMetadata meta = videoService.getVideoMetadata(List.of("vid1"));

        assertNotNull(meta);
        assertEquals("Java Tutorial", meta.getTitles().get(0));
    }

    @Test
    void selectWinner_ShouldReturnExactCount() {
        List<Comment> comments = new ArrayList<>();
        for(int i=0; i<50; i++) comments.add(Comment.builder().authorName("U"+i).build());

        List<Comment> winners = videoService.selectWinner(comments, 5);
        assertEquals(5, winners.size());
    }

    @Test
    void parseCommentsFromJson_ShouldFilterKeywordsCaseInsensitive() {
        String json = createMockYoutubeJson("User1", "This is a LUCKY check", "2023-10-01T10:00:00Z", null);
        List<Comment> result = videoService.parseCommentsFromJson(json, "vid1", "lucky");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).isContainsKeyword());
    }
}