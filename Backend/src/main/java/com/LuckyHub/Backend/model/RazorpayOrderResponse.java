package com.LuckyHub.Backend.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RazorpayOrderResponse {
    private String orderId;
    private BigDecimal amount;
    private String currency;
}
