package com.LuckyHub.Backend.model;

import lombok.*;
        import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiveawayHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private List<String> winners;
    private List<VideoDetailDTO> videoDetails;
    private int winnersCount;
    private int commentCount;
    private String keywordUsed;
    private boolean loyaltyFilterApplied;
    private LocalDateTime createdAt;
}