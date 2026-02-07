package com.LuckyHub.Backend.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class VideoMetadata {
    private List<String> videoIds;
    private List<String> titles;
    private List<String> thumbnailUrls;
    private Set<String> channelIds;
}