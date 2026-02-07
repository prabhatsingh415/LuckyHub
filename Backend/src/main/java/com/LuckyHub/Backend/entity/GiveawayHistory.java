package com.LuckyHub.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiveawayHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> winners;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<VideoDetail> videoDetails;

    private int winnersCount;
    private int commentCount;
    private String keywordUsed;
    private boolean loyaltyFilterApplied;

    private LocalDateTime createdAt;
}