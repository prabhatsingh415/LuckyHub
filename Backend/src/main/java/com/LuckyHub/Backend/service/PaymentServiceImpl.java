package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.PaymentRefundEvent;
import com.LuckyHub.Backend.event.PaymentSuccessfulEvent;
import com.LuckyHub.Backend.exception.*;
import com.LuckyHub.Backend.model.*;
import com.LuckyHub.Backend.repository.PaymentRepository;
import com.razorpay.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

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
    public void createPartialPayment(Long userId, SubscriptionTypes planType, BigDecimal amount, String currency, String orderId, String receiptId) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPlanType(planType);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setOrderId(orderId);
        payment.setReceiptId(receiptId);
        payment.setStatus(PaymentStatus.PENDING);
    paymentRepo.save(payment);
}

    @Override
    @Transactional
    public void completePayment(String orderId, String paymentId, boolean signatureVerified, LocalDateTime paymentDate, User user, String planByAmount, BigDecimal amount, String paymentMethod) {
        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment record not found " + orderId));

        payment.setPaymentId(paymentId);
        payment.setSignatureVerified(signatureVerified);
        payment.setPaymentDate(paymentDate);
        payment.setStatus(signatureVerified ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setMethod(paymentMethod);
        paymentRepo.save(payment);
        paymentRepo.deleteByUserIdAndStatusAndIdNot(
                payment.getUserId(),
                PaymentStatus.SUCCESS,
                payment.getId()
        );
        subscriptionService.upgradeSubscription(user, payment);
        log.info("Trying to send the email........");
        // Sending payment successful mail
        publisher.publishEvent(new PaymentSuccessfulEvent(
                user,
                paymentId,
                orderId,
                amount,
                planByAmount
        ));
    }


@Override
public LastPaymentModel getLastPayment(User user) {

    Payment payment = paymentRepo.findFirstByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), PaymentStatus.SUCCESS)
            .orElseThrow(() -> new PaymentNotFoundException("No payment found for this user!"));

    if (payment.getPaymentDate() == null) {
        log.error("Data Integrity Error: Payment {} is SUCCESS but has no paymentDate", payment.getOrderId());
        throw new IllegalStateException("Payment date is missing for successful transaction.");
    }

    LocalDate startDate = payment.getPaymentDate().toLocalDate();
    LocalDate endDate = startDate.plusMonths(1);
    BigDecimal amountInRupees = payment.getAmount();
    return LastPaymentModel.builder()
            .paymentId(payment.getPaymentId())
            .amount(amountInRupees)
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
public boolean processPaymentForCompletion(PaymentVerificationRequest request, User user) {

    try {
        String orderId  = request.getRazorpay_order_id();
        String paymentId = request.getRazorpay_payment_id();
        String signature = request.getRazorpay_signature();

        Payment payment = paymentRepo.findByOrderId(orderId)
                          .orElseThrow(() -> new PaymentNotFoundException("Payment not found !"));

        long amountInPaise = payment.getAmount().multiply(new BigDecimal(100)).longValue();

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
            com.razorpay.Payment capturedPayment = razorpayClient.payments.capture(paymentId, captureRequest);//capture the payment
            String paymentMethod = capturedPayment.get("method");

            this.completePayment(orderId, paymentId, true, LocalDateTime.now(), user, payment.getPlanType().toString(), payment.getAmount(), paymentMethod); // complete the payment
        } else {
            return false;
        }

    } catch (Exception e) {
        return false;
    }

    return true;
}

@Override
public RazorpayOrderResponse initializePayment(User user, String planName) {
    SubscriptionTypes currPlan = (user.getSubscription() != null)
            ? user.getSubscription().getSubscriptionType()
            : SubscriptionTypes.FREE;

    SubscriptionTypes requestedPlan;
    try {
        requestedPlan = SubscriptionTypes.valueOf(planName);
    } catch (IllegalArgumentException e) {
        throw new InvalidPlanNameException("Invalid Plan Name!");
    }

    if (requestedPlan.getPrice() <= currPlan.getPrice()) {
        if (currPlan == SubscriptionTypes.DIAMOND) {
            throw new SubscriptionDowngradeException("You are already on the Max Tier!");
        }
        throw new SubscriptionDowngradeException("Downgrade or re-purchase of " + currPlan + " is not allowed!");
    }

    BigDecimal subAmount = BigDecimal.valueOf(requestedPlan.getPrice());
    BigDecimal amountInPaiseBD = subAmount.multiply(new BigDecimal("100"));
    long finalAmountInPaise = amountInPaiseBD.longValue();

    String receiptId = "LHN_" + user.getId() + "_" + System.currentTimeMillis();
    JSONObject obj = new JSONObject();
    obj.put("amount", finalAmountInPaise);
    obj.put("currency", "INR");
    obj.put("receipt", receiptId);
    obj.put("payment_capture", 0);
    Order order;
    try {
        order = razorpayClient.orders.create(obj);
        log.info("Order created for {}",user.getId());
    } catch (RazorpayException e) {
        log.info("Order creation failed for {}", user.getId());
        throw new PaymentGatewayException("Razorpay order creation failed!");
    }

    createPartialPayment(user.getId(), requestedPlan, subAmount, "INR", order.get("id"), receiptId);

    return RazorpayOrderResponse.builder()
                                .orderId(order.get("id"))
                                .amount(subAmount)
                                .currency("INR").build();
}

@Override
@Transactional
public boolean processAndVerify(User user, PaymentVerificationRequest request) {
    boolean verifiedNow = this.processPaymentForCompletion(request, user);
    boolean isAlreadySucceed = this.checkIsPaymentSuccess(request.getRazorpay_order_id());

    return verifiedNow || isAlreadySucceed;
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
            long amountInPaise = paymentEntity.getLong("amount");

            BigDecimal amountInRupees = BigDecimal.valueOf(amountInPaise)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            Payment payment = paymentRepo.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException("Payment not found !"));

            if (PaymentStatus.SUCCESS == payment.getStatus()) {
                log.info("Order {} already processed successfully. Skipping webhook.", orderId);
                return;
            }

            User user = userService.findUserByUserId(payment.getUserId());
            SubscriptionTypes planByAmount = subscriptionService.getPlanByAmount(amountInRupees);

            try {
                JSONObject captureRequest = new JSONObject();
                captureRequest.put("amount", amountInPaise);
                captureRequest.put("currency", "INR");
                com.razorpay.Payment capturedPayment = razorpayClient.payments.capture(paymentId, captureRequest);
                String paymentMethod = capturedPayment.get("method");
                this.completePayment(orderId, paymentId, true, LocalDateTime.now(), user, planByAmount.toString(), BigDecimal.valueOf(amountInPaise), paymentMethod);
            log.info("Webhook Success: Payment captured for order {}", orderId);
            }catch (Exception e){
                log.error("Webhook Error: Capture failed for {}. Reason: {}", orderId, e.getMessage());
                this.markPaymentFailed(orderId);
                publisher.publishEvent(new PaymentRefundEvent(
                      user,
                      planByAmount.toString(),
                      orderId,
                      amountInRupees
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

@Override
public boolean checkIsPaymentSuccess(String orderId) {
    log.info("[Payment-Service:]Checking payment status for orderId{}", orderId );
    Payment payment = paymentRepo.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException("Payment not found!"));
    return payment.getStatus() == PaymentStatus.SUCCESS;
}

@Override
@Transactional
public void deletePayment(long userId) {
    paymentRepo.deleteByUserId(userId);
}

@Transactional
@Scheduled(cron = "0 0 0 * * *")  // cron job for DB cleanup
public void cleanJunk() {
    log.info("[Payment-CRON] Starting junk payment cleanup...");
    LocalDateTime threshold = LocalDateTime.now().minusHours(24);

    paymentRepo.deleteByStatusAndCreatedAtBefore(PaymentStatus.PENDING, threshold);
    paymentRepo.deleteByStatusAndCreatedAtBefore(PaymentStatus.FAILED, threshold);

    log.info("[Payment-CRON] Cleanup finished.");
}


@Scheduled(cron = "0 0/15 * * * *")
public void reconcileStuckPayments() { // cron job for payment reconciliation
    log.info("[Payment-CRON] Starting payment reconciliation job...");

    LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
    List<Payment> stuckPayments = paymentRepo.findAllByStatusAndCreatedAtBefore(PaymentStatus.PENDING, threshold);

    for (Payment payment : stuckPayments) {
        String orderId = payment.getOrderId();
        String planName = payment.getPlanType().toString();
        BigDecimal amountInRupee = payment.getAmount();
        User user = null;

        try {
            user = userService.getUserById(payment.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found!"));

            List<com.razorpay.Payment> rzpPayments = razorpayClient.orders.fetchPayments(orderId);
            boolean isResolved = false;

            for (com.razorpay.Payment rzpPayment : rzpPayments) {
                String rzpStatus = rzpPayment.get("status");
                String rzpPaymentId = rzpPayment.get("id");
                String rzpMethod = rzpPayment.get("method");

                if ("captured".equals(rzpStatus)) {
                    log.info("[Payment-CRON] Syncing already captured payment for Order: {}", orderId);
                    this.completePayment(orderId, rzpPaymentId, true, LocalDateTime.now(), user, planName, amountInRupee, rzpMethod);
                    isResolved = true;
                    break;
                }
                else if ("authorized".equals(rzpStatus)) {
                    log.info("[Payment-CRON] Capturing authorized payment for Order: {}", orderId);
                    long rzpAmountInPaise = amountInRupee.multiply(new BigDecimal("100")).longValue();

                    JSONObject captureRequest = new JSONObject();
                    captureRequest.put("amount", rzpAmountInPaise);
                    captureRequest.put("currency", "INR");

                    com.razorpay.Payment captured = razorpayClient.payments.capture(rzpPaymentId, captureRequest);
                    String capturedMethod = captured.get("method");

                    this.completePayment(orderId, rzpPaymentId, true, LocalDateTime.now(), user, planName, amountInRupee, capturedMethod);
                    isResolved = true;
                    break;
                }
            }

            if (!isResolved && !rzpPayments.isEmpty()) {
                throw new PaymentGatewayException("Payment attempted but not successful/authorized on Razorpay.");
            }

        } catch (Exception e) {
            log.error("[Payment-CRON] Error processing order {}: {}", orderId, e.getMessage());
            this.markPaymentFailed(orderId);

            if (user != null) {
                publisher.publishEvent(new PaymentRefundEvent(user, planName, orderId, amountInRupee));
                log.info("[Payment-CRON] Refund/Failure mail sent for order {}", orderId);
            }
        }
    }
}
}

