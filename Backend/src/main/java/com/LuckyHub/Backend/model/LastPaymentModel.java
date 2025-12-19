package com.LuckyHub.Backend.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LastPaymentModel {

    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private SubscriptionTypes subscriptionType;

    private LocalDate periodStart;
    private LocalDate periodEnd;

    private LocalDate nextBillingDate;
}
