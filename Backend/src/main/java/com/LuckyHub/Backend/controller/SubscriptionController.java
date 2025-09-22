package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.InvalidAmountForAnyPlanException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.SubscriptionTypes;
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

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final PaymentService paymentService;


    public SubscriptionController(SubscriptionService subscriptionService, UserService userService, PaymentService paymentService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @Value("${Razorpay_key_Id}")
    private String key;

    @Value("${Razorpay_key_secret}")
    private String secret;

    @PostMapping("/proceed")
    public ResponseEntity<?> proceedPayment(HttpServletRequest request, @RequestBody Map<String, Object> data) throws RazorpayException {

        BigDecimal subAmount;
        try {
            Object amountObj = data.get("amount");
            if(amountObj == null) throw new InvalidAmountForAnyPlanException("Amount is required");
            subAmount = new BigDecimal(amountObj.toString());
        } catch (NumberFormatException e) {
            throw new InvalidAmountForAnyPlanException("Invalid amount format");
        }


        if(!subscriptionService.verifyTheAmount(subAmount.intValue())) {
            throw new InvalidAmountForAnyPlanException("The given amount didn't match with any plan!");
        }


        RazorpayClient razorpayClient = new RazorpayClient(key, secret);


        Long userId = getUserId(request);
        if(userId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "User Not Found!"));
        }


        String receiptId = "LHN_" + userId + "_" + LocalDate.now();


        JSONObject obj = new JSONObject();
        obj.put("amount", subAmount.multiply(BigDecimal.valueOf(100)).longValue()); // in paise
        obj.put("currency", "INR");
        obj.put("receipt", receiptId);

        Order order = razorpayClient.orders.create(obj);

        SubscriptionTypes planType = subscriptionService.getPlanByAmount(subAmount.intValue());

        paymentService.createPartialPayment(userId, planType, subAmount, "INR", order.get("id"), receiptId);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "orderId", order.get("id"),
                        "amount", subAmount,
                        "currency", "INR",
                        "message","Order created successfully!"
                )
        );
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(HttpServletRequest request, @RequestBody Map<String, Object> data) {

        Long userId = getUserId(request);
        Optional<User> user = userService.getUserById(userId);

        if (user.isEmpty()) throw new UserNotFoundException("User Not Found For Given Id");

        try {
            String orderId = data.get("razorpay_order_id").toString();
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
                userService.upgradeSubscription(paymentId);

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Payment verified successfully!"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "failed",
                        "message", "Payment verification failed!"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    public Long getUserId(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);

        Map<String, Object> userData = userService.getCurrentUserFromToken(token);

        Long ID = userService.findUserIdByEmail(userData.get("email").toString());

        return ID;
    }

}
