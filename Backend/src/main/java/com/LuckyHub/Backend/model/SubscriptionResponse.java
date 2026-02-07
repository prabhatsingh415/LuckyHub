package com.LuckyHub.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionResponse {

    private String planName;
    private String status;

    private Date startDate;
    private Date expiryDate;

    private Integer maxComments;
    private Integer maxWinners;
    private Integer remainingGiveaways;


    private boolean isActive;
    private String paymentReference;
}
