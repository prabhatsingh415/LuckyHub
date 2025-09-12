package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.exception.VideosFromDifferentChannelsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService{

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    @Override
    public Map<String, List<String>> fetchComments(List<String> videoIds, String keyword) {
        return Map.of();
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

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        if(items == null || items.isEmpty()) {
            throw new RuntimeException("Video not found: " + videoId);
        }

        Map<String, Object> snippet = (Map<String, Object>) items.get(0).get("snippet");
        return (String) snippet.get("channelId");
    }

}
