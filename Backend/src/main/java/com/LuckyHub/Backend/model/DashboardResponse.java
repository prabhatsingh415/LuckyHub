package com.LuckyHub.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;

    private String subscriptionType;
    private String subscriptionStatus;

    private Date createdAt;
    private int winnersSelectedThisMonth;

    private int maxGiveaways;
    private int remainingGiveaways;
    private int maxComments;
    private int maxWinners;

    private Long subscriptionExpiryDate;
    private boolean isSubscriptionExpired;
}