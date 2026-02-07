package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.Comment;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.model.VideoMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService{

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public VideoServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Comment> fetchComments(List<String> videoIds, String keyword, SubscriptionTypes plan) {
        int maxCommentsLimit = plan.getMaxComments();

        Map<String, Comment> authorMap = new HashMap<>();
        int totalFetchedAcrossAllVideos = 0;

        for (String videoId : videoIds) {
            if (totalFetchedAcrossAllVideos >= maxCommentsLimit) break;
            String nextPageToken = null;

            do {
                String url = String.format(
                        "https://youtube.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId=%s&maxResults=100&key=%s%s",
                        videoId, apiKey, (nextPageToken != null ? "&pageToken=" + nextPageToken : "")
                );


                String json = restTemplate.getForObject(url, String.class);
                List<Comment> comments = parseCommentsFromJson(json, videoId, keyword);

                // Global limit check
                int remainingGlobal = maxCommentsLimit - totalFetchedAcrossAllVideos;
                if (comments.size() > remainingGlobal) {
                    comments = comments.subList(0, remainingGlobal);
                }

                for (Comment c : comments) {
                    String key = c.getAuthorName();
                    authorMap.merge(key, c, (existing, incoming) -> {

                        existing.setFrequency(existing.getFrequency() + 1);

                        existing.getParticipatedVideoIds().add(incoming.getVideoId());

                        if (incoming.getEarliestCommentTime().isBefore(existing.getEarliestCommentTime())) {
                            existing.setEarliestCommentTime(incoming.getEarliestCommentTime());
                        }
                        return existing;
                    });
                }

                totalFetchedAcrossAllVideos += comments.size();
                nextPageToken = extractNextPageToken(json);
            } while (nextPageToken != null && totalFetchedAcrossAllVideos < maxCommentsLimit);
        }

        return authorMap.values().stream()
                .sorted(Comparator
                        // 1. Give priority to users who engaged with more unique videos
                        .comparingInt((Comment c) -> c.getParticipatedVideoIds().size()).reversed()
                        // 2. If video count is same, rank by total number of comments (Frequency)
                        .thenComparing(Comparator.comparingInt(Comment::getFrequency).reversed())
                        // 3. Final tie-breaker: The user who commented the earliest
                        .thenComparing(Comment::getEarliestCommentTime))
                .toList();
    }

    @Override
    public List<Comment> parseCommentsFromJson(String json, String videoId, String keyword) {
        List<Comment> comments = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.get("items");

            if(items != null && items.isArray()){
                for(JsonNode item : items){
                    JsonNode topLevelComment = item.get("snippet").get("topLevelComment");
                    JsonNode snippet = topLevelComment.get("snippet");

                    String commentId = topLevelComment.get("id").asText();
                    String message = snippet.get("textDisplay").asText();
                    Instant publishedAt = Instant.parse(snippet.get("publishedAt").asText());

                    // Keyword logic check
                    boolean containsKeyword = keyword != null && !keyword.isEmpty()
                            && message.toLowerCase().contains(keyword.toLowerCase());

                    Comment comment = Comment.builder()
                            .commentId(commentId)
                            .videoId(videoId)
                            .authorName(snippet.get("authorDisplayName").asText())
                            .authorProfileImageUrl(snippet.get("authorProfileImageUrl").asText())
                            .authorChannelUrl(snippet.get("authorChannelUrl").asText())
                            .commentUrl("https://www.youtube.com/watch?v=" + videoId + "&lc=" + commentId)
                            .message(message)
                            .publishedAt(publishedAt)
                            .containsKeyword(containsKeyword)
                            .frequency(1)
                            .participatedVideoIds(new HashSet<>(Set.of(videoId)))
                            .earliestCommentTime(publishedAt)
                            .build();

                    comments.add(comment);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong while parsing comments!", e);
        }
        return comments;
    }

    @Override
    public String extractNextPageToken(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode tokenNode = root.get("nextPageToken");
            if(tokenNode != null){
                return tokenNode.asText();
            }
        } catch (Exception e) {
           throw new RuntimeException("Something went wrong !");
        }
        return null;
    }

    @Override
    public List<Comment> selectWinner(List<Comment> fetchedComments, int numberOfWinners) {
        if (fetchedComments == null || fetchedComments.isEmpty()) {
            return new ArrayList<>();
        }

        int poolSize = Math.min(fetchedComments.size(), Math.max(numberOfWinners * 2, 20));
        List<Comment> candidatePool = new ArrayList<>(fetchedComments.subList(0, poolSize));

        Collections.shuffle(candidatePool);

        return candidatePool.stream()
                .limit(numberOfWinners)
                .toList();
    }



    @Override
    public VideoMetadata getVideoMetadata(List<String> videoIds) {
        log.info("Youtube api request reached !");
        if (videoIds.isEmpty()) return null;

        String joinedIds = String.join(",", videoIds);
        String url = String.format("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=%s&key=%s", joinedIds, apiKey);

        try {
            log.info("Data fetched !");
            JsonNode root = new ObjectMapper().readTree(restTemplate.getForObject(url, String.class));
            JsonNode items = root.get("items");

            List<String> titles = new ArrayList<>();
            List<String> thumbnails = new ArrayList<>();
            Set<String> channelIds = new HashSet<>();

            for (JsonNode item : items) {
                JsonNode snippet = item.get("snippet");
                titles.add(snippet.get("title").asText());
                thumbnails.add(snippet.get("thumbnails").get("medium").get("url").asText());
                channelIds.add(snippet.get("channelId").asText());
            }

            return VideoMetadata.builder()
                    .videoIds(videoIds)
                    .titles(titles)
                    .thumbnailUrls(thumbnails)
                    .channelIds(channelIds)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("YouTube Metadata fetch failed!");
        }
    }

    @Override
    public String extractVideoId(String url) {
        String videoId = null;

        if(url.contains("youtu.be/")) {
            videoId = url.substring(url.lastIndexOf("/") + 1);
        } else if(url.contains("watch?v=")) {
            int index = url.indexOf("v=") + 2;
            videoId = url.substring(index);
        }

        if(videoId != null && videoId.contains("&")) {
            videoId = videoId.substring(0, videoId.indexOf("&"));
        }
        if(videoId != null && videoId.contains("?")) {
            videoId = videoId.substring(0, videoId.indexOf("?"));
        }

        if(videoId != null && videoId.length() > 11){
            videoId = videoId.substring(0, 11);
        }

        log.info("[Video Service]:video Id extracted successfully !");
        return videoId;
    }

}
