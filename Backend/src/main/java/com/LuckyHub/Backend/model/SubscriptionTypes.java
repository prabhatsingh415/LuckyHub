package com.LuckyHub.Backend.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionTypes {
    FREE(0, 300, 2, 3),
    GOLD(49, 600, 5, 10),
    DIAMOND(79, 1000, 10, -1);

    private final int price;        // ₹ per month
    private final int maxComments;  // max comments per giveaway
    private final int maxWinners;   // winners per giveaway
    private final int maxGiveaways; // giveaways per month (-1 = unlimited)
}
