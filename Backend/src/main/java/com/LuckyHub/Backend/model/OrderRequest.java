package com.LuckyHub.Backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "Plan name is required")
    private String planName;
}