package com.LuckyHub.Backend.controller;


import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.InvalidAmountForAnyPlanException;
import com.LuckyHub.Backend.exception.SubscriptionDowngradeException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.service.JWTService;
import com.LuckyHub.Backend.service.PaymentService;
import com.LuckyHub.Backend.service.SubscriptionService;
import com.LuckyHub.Backend.service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final JWTService jwtService;

    public SubscriptionController(SubscriptionService subscriptionService, UserService userService, PaymentService paymentService, JWTService jwtService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.jwtService = jwtService;
    }

    @Value("${Razorpay_key_Id}")
    private String key;

    @Value("${Razorpay_key_secret}")
    private String secret;

    @PostMapping("/createOrder")
    public ResponseEntity<?> proceedPayment(HttpServletRequest request, @RequestBody Map<String, String> data) throws RazorpayException {

        Long userId = userService.getUserIdByRequest(request);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        SubscriptionTypes currPlan = (user.getSubscription() != null)
                ? user.getSubscription().getSubscriptionType()
                : SubscriptionTypes.FREE;

        String planName = data.get("planName").toUpperCase();
        SubscriptionTypes requestedPlan;
        try {
            requestedPlan = SubscriptionTypes.valueOf(planName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Plan Name!"));
        }

        if (requestedPlan.getPrice() <= currPlan.getPrice()) {
            if (currPlan == SubscriptionTypes.DIAMOND) {
                throw new SubscriptionDowngradeException("You are already on the Max Tier!");
            }
            throw new SubscriptionDowngradeException("Downgrade or re-purchase of " + currPlan + " is not allowed!");
        }

        int subAmount = requestedPlan.getPrice();

        RazorpayClient razorpayClient = new RazorpayClient(key, secret);

        String receiptId = "LHN_" + userId + "_" + System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        obj.put("amount", subAmount * 100);
        obj.put("currency", "INR");
        obj.put("receipt", receiptId);

        Order order = razorpayClient.orders.create(obj);

        paymentService.createPartialPayment(userId, requestedPlan, BigDecimal.valueOf(subAmount), "INR", order.get("id"), receiptId);

        return ResponseEntity.ok(Map.of(
                "orderId", order.get("id"),
                "amount", subAmount,
                "currency", "INR"
        ));
    }


    @PostMapping("/verifyPayment")
    public ResponseEntity<?> verifyPayment(HttpServletRequest request, @RequestBody Map<String, Object> data) {

        Long userId = userService.getUserIdByRequest(request);
        Optional<User> user = userService.getUserById(userId);

        if (user.isEmpty()) throw new UserNotFoundException("User Not Found!");
        String orderId = "";
        try {
            orderId  = data.get("razorpay_order_id").toString();
            String paymentId = data.get("razorpay_payment_id").toString();
            String signature = data.get("razorpay_signature").toString();
            String payload = orderId + "|" + paymentId;

            // HMAC SHA256 using Razorpay secret
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            String expectedSignature = new String(Hex.encodeHex(hash));

            if (expectedSignature.equals(signature)) {
                // Payment verified successfully
                paymentService.completePayment(orderId, paymentId, true, LocalDateTime.now());

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Payment verified successfully!"
                ));
            } else {
                paymentService.markPaymentFailed(orderId);
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "failed",
                        "message", "Payment verification failed!"
                ));
            }

        } catch (Exception e) {
            if (orderId != null && !orderId.isEmpty()) {
                paymentService.markPaymentFailed(orderId);
            }
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
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
