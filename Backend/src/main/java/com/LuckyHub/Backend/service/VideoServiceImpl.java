package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.exception.PlansGiveawayLimitExceedException;
import com.LuckyHub.Backend.exception.VideosFromDifferentChannelsException;
import com.LuckyHub.Backend.model.Comment;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService{

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public VideoServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Comment> fetchComments(List<String> videoURLs, String keyword, SubscriptionTypes plan) {
        int maxCommentsLimit = plan.getMaxComments();
        int maxGiveaways = plan.getMaxGiveaways();

        if (maxGiveaways == 0) {
            throw new PlansGiveawayLimitExceedException(
                    "You have reached the maximum giveaway limit for your plan. Upgrade to get more giveaways!");
        }

        List<String> videoIds = new ArrayList<>();
        for(String url : videoURLs){
            videoIds.add(extractVideoId(url));
        }

        Map<String, Comment> authorVideoMap = new HashMap<>();

        for (String videoId : videoIds) {
            int totalFetched = 0;
            String nextPageToken = null;

            do {
                String url = "https://youtube.googleapis.com/youtube/v3/commentThreads" +
                        "?part=snippet,replies" +
                        "&videoId=" + videoId +
                        "&maxResults=100" +
                        (nextPageToken != null ? "&pageToken=" + nextPageToken : "") +
                        "&key=" + apiKey;

                String json = restTemplate.getForObject(url, String.class);

                List<Comment> comments = parseCommentsFromJson(json, videoId, keyword);

                // Keyword filter
                if (keyword != null && !keyword.isEmpty()) {
                    String lowerKey = keyword.toLowerCase();
                    comments = comments.stream()
                            .filter(c -> c.getMessage().toLowerCase().contains(lowerKey))
                            .toList();
                }


                int remaining = maxCommentsLimit - totalFetched;
                if (comments.size() > remaining) {
                    comments = comments.subList(0, remaining);
                }

                for (Comment c : comments) {
                    String key = c.getAuthorName() + "|" + c.getVideoId();
                    authorVideoMap.merge(key, c, (existing, incoming) -> {
                        existing.setFrequency(existing.getFrequency() + 1);
                        return existing;
                    });
                }

                totalFetched += comments.size();
                nextPageToken = extractNextPageToken(json);

            } while (nextPageToken != null && totalFetched < maxCommentsLimit);
        }

        return new ArrayList<>(authorVideoMap.values());
    }


    public List<Comment> parseCommentsFromJson(String json, String videoId, String keyword) {
        List<Comment> comments = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.get("items");

            if(items != null && items.isArray()){
                for(JsonNode item : items){
                    JsonNode snippet = item.get("snippet").get("topLevelComment").get("snippet");
                    String message = snippet.get("textDisplay").asText();
                    boolean containsKeyword = keyword != null && !keyword.isEmpty()
                            && message.toLowerCase().contains(keyword.toLowerCase());

                    Comment comment = Comment.builder()
                            .commentId(item.get("id").asText())
                            .videoId(videoId)
                            .authorName(snippet.get("authorDisplayName").asText())
                            .message(message)
                            .publishedAt(Instant.parse(snippet.get("publishedAt").asText()))
                            .containsKeyword(containsKeyword)
                            .frequency(1)
                            .build();
                    comments.add(comment);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Something went wrong while parsing comments!", e);
        }

        return comments;
    }


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
    public boolean verifySameUser(List<String> videoLinks) {
        String channelId = null;

        for(String link : videoLinks) {
            String videoId = extractVideoId(link);
            String currentChannelId = getChannelIdFromVideo(videoId);

            if(channelId == null) {
                channelId = currentChannelId;
            } else if(!channelId.equals(currentChannelId)) {
                throw new VideosFromDifferentChannelsException("All videos must be from the same channel");
            }
        }

        return true;
    }

    @Override
    public List<Comment> selectWinner(List<Comment> fetchedComments, int numberOfWinners) {
        Map<String, Comment> authorMap = new HashMap<>();
        Random random = new Random();

        for (Comment comment : fetchedComments) {
            authorMap.merge(comment.getAuthorName() + "|" + comment.getVideoId(), comment, (existing, incoming) ->
                    existing.getFrequency() > incoming.getFrequency() ? existing :
                            existing.getFrequency() < incoming.getFrequency() ? incoming :
                                    existing.getPublishedAt().isBefore(incoming.getPublishedAt()) ? existing :
                                            incoming.getPublishedAt().isBefore(existing.getPublishedAt()) ? incoming :
                                                    random.nextBoolean() ? existing : incoming
            );
        }

        PriorityQueue<Comment> pq = new PriorityQueue<>(
                (c1, c2) -> {
                    int freqCompare = Integer.compare(c2.getFrequency(), c1.getFrequency());
                    if (freqCompare != 0) return freqCompare;
                    return c1.getPublishedAt().compareTo(c2.getPublishedAt());
                }
        );

        pq.addAll(authorMap.values());

        List<Comment> winners = new ArrayList<>();
        while (!pq.isEmpty() && winners.size() < numberOfWinners) {
            winners.add(pq.poll());
        }

        return winners;
    }

    private String extractVideoId(String url) {
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

        return videoId;
    }


    private String getChannelIdFromVideo(String videoId) {
        String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="
                + videoId + "&key=" + apiKey;

        RestTemplate request = restTemplate;
        Map<String, Object> response = request.getForObject(apiUrl, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        if(items == null || items.isEmpty()) {
            throw new RuntimeException("Video not found: " + videoId);
        }

        Map<String, Object> snippet = (Map<String, Object>) items.get(0).get("snippet");
        return (String) snippet.get("channelId");
    }

}
