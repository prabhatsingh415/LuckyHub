package com.LuckyHub.Backend.entity;

import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionTypes subscriptionType;

    private Date startDate;
    private Date expiringDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.NONE;

    private String paymentId;

    @Column(nullable = false)
    private Integer maxComments = 300;

    @Column(nullable = false)
    private Integer maxWinners = 2;

    @Column(nullable = false)
    private Integer remainingGiveaways = 3;

    @OneToOne(mappedBy = "subscription")
    @JsonBackReference
    private User user;
}
