package com.LuckyHub.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private String commentId;
    private String videoId;
    private String authorName;
    private String authorProfileImageUrl;
    private String authorChannelUrl;
    private String commentUrl;
    private String message;
    private Instant publishedAt;
    private boolean containsKeyword;
    private Set<String> participatedVideoIds = new HashSet<>();
    private Instant earliestCommentTime;
    private int frequency;
}
