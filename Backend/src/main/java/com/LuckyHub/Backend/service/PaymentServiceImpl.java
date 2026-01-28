package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.PaymentRefundEvent;
import com.LuckyHub.Backend.event.PaymentSuccessfulEvent;
import com.LuckyHub.Backend.exception.PaymentGatewayException;
import com.LuckyHub.Backend.exception.PaymentNotFoundException;
import com.LuckyHub.Backend.exception.SubscriptionDowngradeException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.LastPaymentModel;
import com.LuckyHub.Backend.model.PaymentStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.PaymentRepository;
import com.razorpay.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
    public class PaymentServiceImpl implements PaymentService {

        @Value("${Razorpay_key_Id}")
        private String key;

        @Value("${Razorpay_key_secret}")
        private String secret;

        @Value("${razorpayWebhookSecret}")
        private String webhookSecret;

        private final PaymentRepository paymentRepo;
        private final UserService userService;
        private final SubscriptionService subscriptionService;
        private final RazorpayClient razorpayClient;
        private final ApplicationEventPublisher publisher;

        public PaymentServiceImpl(PaymentRepository paymentRepo, @Lazy UserService userService, SubscriptionService subscriptionService, RazorpayClient razorpayClient, ApplicationEventPublisher publisher) {
            this.paymentRepo = paymentRepo;
            this.userService = userService;
            this.subscriptionService = subscriptionService;
            this.razorpayClient = razorpayClient;
            this.publisher = publisher;
        }

    @Override
    @Transactional
        public Payment createPartialPayment(Long userId, SubscriptionTypes planType, BigDecimal amount, String currency, String orderId, String receiptId) {
            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setPlanType(planType);
            payment.setAmount(amount);
            payment.setCurrency(currency);
            payment.setOrderId(orderId);
            payment.setReceiptId(receiptId);
            payment.setStatus(PaymentStatus.PENDING);
            return paymentRepo.save(payment);
        }

        @Override
        @Transactional
        public Payment completePayment(String orderId, String paymentId, boolean signatureVerified, LocalDateTime paymentDate, User user, String planByAmount, int amount) {
            Payment payment = paymentRepo.findByOrderId(orderId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment record not found " + orderId));

            payment.setPaymentId(paymentId);
            payment.setSignatureVerified(signatureVerified);
            payment.setPaymentDate(paymentDate);
            payment.setStatus(signatureVerified ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            paymentRepo.save(payment);
            paymentRepo.deleteByUserIdAndStatusAndIdNot(
                    payment.getUserId(),
                    PaymentStatus.SUCCESS,
                    payment.getId()
            );
            subscriptionService.upgradeSubscription(payment);
            log.info("Trying to send the email........");
            // Sending payment successful mail
            publisher.publishEvent(new PaymentSuccessfulEvent(
                    user,
                    paymentId,
                    orderId,
                    amount,
                    planByAmount
            ));
            return payment;
        }

        @Override
        public Payment getPaymentByOrderId(String orderId) {
            return paymentRepo.findByOrderId(orderId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        }

    @Override
    public Payment getPaymentDataToUpgradeService(String paymentId) {
        return paymentRepo.findByPaymentId(paymentId);
    }

    @Override
    public LastPaymentModel getLastPayment(String email) {

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found !"));

        Payment payment = paymentRepo.findByUserId(user.getId())
                .orElseThrow(() -> new PaymentNotFoundException("Last payment not found !"));

        LocalDate startDate = payment.getPaymentDate().toLocalDate();
        LocalDate endDate = startDate.plusMonths(1);

        return LastPaymentModel.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .subscriptionType(payment.getPlanType())
                .periodStart(startDate)
                .periodEnd(endDate)
                .nextBillingDate(endDate)
                .build();
    }

    @Override
    @Transactional
    public void markPaymentFailed(String orderId) {
        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for Order ID: " + orderId));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepo.save(payment);
    }

    @Override
    public boolean processPaymentForCompletion(Map<String, Object> data, User user) {

        try {
            String orderId  = data.get("razorpay_order_id").toString();
            String paymentId = data.get("razorpay_payment_id").toString();
            String signature = data.get("razorpay_signature").toString();

            Payment payment = paymentRepo.findByOrderId(orderId)
                              .orElseThrow(() -> new PaymentNotFoundException("Payment not found !"));

            int amountInPaise = payment.getAmount().multiply(new BigDecimal(100)).intValue();

            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            boolean isValid = Utils.verifyPaymentSignature(attributes, secret);

            if (isValid) {
                // Payment verified successfully
                JSONObject captureRequest = new JSONObject();
                captureRequest.put("amount", amountInPaise);
                captureRequest.put("currency", "INR");
                razorpayClient.payments.capture(paymentId, captureRequest);  //capture the payment

                this.completePayment(orderId, paymentId, true, LocalDateTime.now(), user, payment.getPlanType().toString(), amountInPaise/100); // complete the payment
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public Map<String, Object> initializePayment(Long userId, String planName) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        SubscriptionTypes currPlan = (user.getSubscription() != null)
                ? user.getSubscription().getSubscriptionType()
                : SubscriptionTypes.FREE;

        SubscriptionTypes requestedPlan;
        try {
            requestedPlan = SubscriptionTypes.valueOf(planName);
        } catch (IllegalArgumentException e) {
            return Map.of("message", "Invalid Plan Name!");
        }

        if (requestedPlan.getPrice() <= currPlan.getPrice()) {
            if (currPlan == SubscriptionTypes.DIAMOND) {
                throw new SubscriptionDowngradeException("You are already on the Max Tier!");
            }
            throw new SubscriptionDowngradeException("Downgrade or re-purchase of " + currPlan + " is not allowed!");
        }

        int subAmount = requestedPlan.getPrice();

        String receiptId = "LHN_" + userId + "_" + System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        obj.put("amount", subAmount * 100);
        obj.put("currency", "INR");
        obj.put("receipt", receiptId);
        obj.put("payment_capture", 0);
        Order order = null;
        try {
            order = razorpayClient.orders.create(obj);
            log.info("Order created for {}",userId);
        } catch (RazorpayException e) {
            log.info("Order creation failed for {}", userId);
            throw new PaymentGatewayException("Razorpay order creation failed!");
        }


        createPartialPayment(userId, requestedPlan, BigDecimal.valueOf(subAmount), "INR", order.get("id"), receiptId);

      return Map.of(
                "orderId", order.get("id"),
                "amount", subAmount,
                "currency", "INR"
        );
    }

    @Override
    public void processRazorpayWebhook(String payload, String signature) {
          String orderId = "";
        try {
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            if (!isValid) {
                log.error("Security alert: Invalid Webhook Signature!");
                throw new PaymentGatewayException("Invalid Webhook Signature!");
            }

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");

            if("payment.authorized".equals(event)){
                JSONObject paymentEntity = json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");


                String paymentId = paymentEntity.getString("id");
                orderId = paymentEntity.getString("order_id");
                int amount = paymentEntity.getInt("amount");

                Payment payment = paymentRepo.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException("Payment not found !"));

                if (PaymentStatus.SUCCESS == payment.getStatus()) {
                    log.info("Order {} already processed successfully. Skipping webhook.", orderId);
                    return;
                }

                User user = userService.findUserByUserId(payment.getUserId());
                SubscriptionTypes planByAmount = subscriptionService.getPlanByAmount(amount/100);

                try {
                    JSONObject captureRequest = new JSONObject();
                    captureRequest.put("amount", amount);
                    captureRequest.put("currency", "INR");
                    razorpayClient.payments.capture(paymentId, captureRequest);

                    this.completePayment(orderId, paymentId, true, LocalDateTime.now(), user, planByAmount.toString(), amount);

                    log.info("Webhook Success: Payment captured for order {}", orderId);
                }catch (Exception e){
                    log.error("Webhook Error: Capture failed for {}. Reason: {}", orderId, e.getMessage());
                    this.markPaymentFailed(orderId);
                    publisher.publishEvent(new PaymentRefundEvent(
                          user,
                          planByAmount.toString(),
                          orderId,
                          amount
                    ));
                    log.info("Webhook Error: Payment Refund mail send for user {}", user);
                }
            } else if ("payment.failed".equals(event)) {
                this.markPaymentFailed(orderId);
                log.info("Gateway level failure for order: {}", orderId);
            }
        } catch (RazorpayException e) {
            log.error("Webhook processing failed: {}", e.getMessage());
            if (orderId != null && !orderId.isEmpty()) {
                this.markPaymentFailed(orderId);
            }
            throw new PaymentGatewayException("Internal server error !");
        }

    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanJunk() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);

        paymentRepo.deleteByStatusAndCreatedAtBefore(PaymentStatus.PENDING, threshold);
        paymentRepo.deleteByStatusAndCreatedAtBefore(PaymentStatus.FAILED, threshold);
    }
}

