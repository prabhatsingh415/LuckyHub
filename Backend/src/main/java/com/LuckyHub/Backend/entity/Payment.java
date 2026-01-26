package com.LuckyHub.Backend.entity;

import com.LuckyHub.Backend.model.PaymentStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionTypes planType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(unique = true)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false, unique = true)
    private String receiptId;

    private boolean signatureVerified;

    private LocalDateTime paymentDate;

    private String method;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
}
