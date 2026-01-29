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
    public ResponseEntity<?> proceedPayment(HttpServletRequest request, @RequestBody Map<String, String> data) throws RazorpayException {

        Long userId = userService.getUserIdByRequest(request);
        String planName = data.get("planName").toUpperCase();

        Map<String, Object> response = paymentService.initializePayment(userId, planName);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verifyPayment")
    public ResponseEntity<?> verifyPayment(HttpServletRequest request, @RequestBody Map<String, Object> data) {

        String orderId = data.get("razorpay_order_id").toString();
        Long userId = userService.getUserIdByRequest(request);

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
    public ResponseEntity<?> getLastPayment(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUserEmail(token);

        return ResponseEntity.ok(paymentService.getLastPayment(email));
    }

    @GetMapping("/getSubscription")
    public ResponseEntity<?> getSubscription(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUserEmail(token);

        return ResponseEntity.ok(subscriptionService.getUserSubscription(email));
    }

}
