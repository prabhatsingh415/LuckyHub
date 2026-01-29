package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final PaymentService paymentService;

    public WebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody String payload,@RequestHeader("X-Razorpay-Signature")String signature){
        log.info("Webhook request received !");
        paymentService.processRazorpayWebhook(payload, signature);
        return ResponseEntity.ok("Received");
    }
}
