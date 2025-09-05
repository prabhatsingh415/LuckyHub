package com.LuckyHub.Backend.entity;

import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "subscription_type",
        nullable = false
    )
    @Enumerated(EnumType.STRING)
    private SubscriptionTypes SubscriptionType;

    private Date startDate;
    private Date expiringDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private String paymentId;

    @OneToOne(mappedBy = "subscription")
    @JsonBackReference
    private User user;
}
