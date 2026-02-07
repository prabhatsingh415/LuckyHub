package com.LuckyHub.Backend.controller;


import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.OrderRequest;
import com.LuckyHub.Backend.model.PaymentVerificationRequest;
import com.LuckyHub.Backend.model.RazorpayOrderResponse;
import com.LuckyHub.Backend.service.PaymentService;
import com.LuckyHub.Backend.service.SubscriptionService;
import com.LuckyHub.Backend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/subscription")
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final UserService userService;

    public SubscriptionController(SubscriptionService subscriptionService, PaymentService paymentService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/createOrder")
    public ResponseEntity<?> proceedPayment(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody OrderRequest request) {
        String planName = request.getPlanName().toUpperCase();
        String email = userDetails.getUsername();
        User user = userService.findUserByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found !"));

        RazorpayOrderResponse response = paymentService.initializePayment(user, planName);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verifyPayment")
    public ResponseEntity<?> verifyPayment(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PaymentVerificationRequest request) {
        log.info("Verifying payment for order: {}", request.getRazorpay_order_id());

        String email = userDetails.getUsername();
        User user = userService.findUserByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found !"));

        boolean isVerified = paymentService.processAndVerify(user, request);

        if (isVerified) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Payment successful and subscription activated."
            ));
        }

        return ResponseEntity.accepted().body(Map.of(
                "status", "pending",
                "message", "Verification pending. Your plan will be active within 5 mins. If the payment fails, a refund will be initiated automatically—check your email for updates."
        ));
    }

    @GetMapping("/lastPayment")
    public ResponseEntity<?> getLastPayment(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        User user = userService.findUserByEmail(email)
                               .orElseThrow(()-> new UserNotFoundException("User not found !"));

        return ResponseEntity.ok(paymentService.getLastPayment(user));
    }

    @GetMapping("/getSubscription")
    public ResponseEntity<?> getSubscription(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        User user = userService.findUserByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found !"));

        return ResponseEntity.ok(subscriptionService.getUserSubscription(user));
    }
}
