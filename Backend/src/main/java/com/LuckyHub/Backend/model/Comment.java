package com.LuckyHub.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private String commentId;
    private String videoId;
    private String authorName;
    private String message;
    private Instant publishedAt;
    private boolean containsKeyword;
    private int frequency;
}
