package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final PaymentService paymentService;

    public WebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody String payload,@RequestHeader("X-Razorpay-Signature")String signature){
        paymentService.processRazorpayWebhook(payload, signature);
        return ResponseEntity.ok("Received");
    }
}
