package com.LuckyHub.Backend.controller;


import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.service.JWTService;
import com.LuckyHub.Backend.service.PaymentService;
import com.LuckyHub.Backend.service.SubscriptionService;
import com.LuckyHub.Backend.service.UserService;
import com.razorpay.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final JWTService jwtService;

    public SubscriptionController(SubscriptionService subscriptionService, UserService userService, PaymentService paymentService, JWTService jwtService, RazorpayClient razorpayClient) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.jwtService = jwtService;
    }

    @PostMapping("/createOrder")
    public ResponseEntity<?> proceedPayment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> data) throws RazorpayException {
        String email = userDetails.getUsername();
        Long userId = userService.findUserIdByEmail(email);
        String planName = data.get("planName").toUpperCase();

        Map<String, Object> response = paymentService.initializePayment(userId, planName);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verifyPayment")
    public ResponseEntity<?> verifyPayment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Object> data) {

        String orderId = data.get("razorpay_order_id").toString();
        String email = userDetails.getUsername();
        Long userId = userService.findUserIdByEmail(email);

        User user = userService.getUserById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found!"));

        boolean verifiedNow = paymentService.processPaymentForCompletion(data, user);
        boolean isAlreadySucceed = paymentService.checkIsPaymentSuccess(orderId);

        if (verifiedNow || isAlreadySucceed) {
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
        return ResponseEntity.ok(paymentService.getLastPayment(email));
    }

    @GetMapping("/getSubscription")
    public ResponseEntity<?> getSubscription(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        return ResponseEntity.ok(subscriptionService.getUserSubscription(email));
    }
}
