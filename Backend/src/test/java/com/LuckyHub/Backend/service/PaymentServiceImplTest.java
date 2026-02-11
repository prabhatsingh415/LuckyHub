package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.event.PaymentRefundEvent;
import com.LuckyHub.Backend.event.PaymentSuccessfulEvent;
import com.LuckyHub.Backend.exception.SubscriptionDowngradeException;
import com.LuckyHub.Backend.model.PaymentStatus;
import com.LuckyHub.Backend.model.PaymentVerificationRequest;
import com.LuckyHub.Backend.model.RazorpayOrderResponse;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.PaymentRepository;
import com.razorpay.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepo;
    @Mock private UserService userService;
    @Mock private SubscriptionService subscriptionService;
    @Mock private RazorpayClient razorpayClient;
    @Mock private ApplicationEventPublisher publisher;

    @Mock private OrderClient orderClient;
    @Mock private PaymentClient paymentClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User sampleUser;
    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "secret", "test_secret");
        ReflectionTestUtils.setField(paymentService, "webhookSecret", "test_webhook_secret");

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setSubscription(Subscription.builder().subscriptionType(SubscriptionTypes.FREE).build());

        samplePayment = new Payment();
        samplePayment.setId(100L);
        samplePayment.setOrderId("order_123");
        samplePayment.setUserId(sampleUser.getId());
        samplePayment.setAmount(new BigDecimal("49"));
        samplePayment.setPlanType(SubscriptionTypes.GOLD);

        // Manual wiring for Razorpay nested mocks
        razorpayClient.orders = orderClient;
        razorpayClient.payments = paymentClient;
    }

    // Verifies that valid signature and successful capture returns true
    @Test
    void processPaymentForCompletion_ShouldReturnTrue_OnSuccessfulCapture() throws Exception {
        // Prepare Request
        PaymentVerificationRequest request = new PaymentVerificationRequest();
        request.setRazorpay_order_id("order_123");
        request.setRazorpay_payment_id("pay_999");
        request.setRazorpay_signature("valid_signature");

        // Mock Repository
        when(paymentRepo.findByOrderId("order_123")).thenReturn(Optional.of(samplePayment));

        // Mock Razorpay Payment Capture
        com.razorpay.Payment mockCapturedPayment = mock(com.razorpay.Payment.class);
        when(mockCapturedPayment.get("method")).thenReturn("upi");
        when(paymentClient.capture(eq("pay_999"), any(JSONObject.class))).thenReturn(mockCapturedPayment);

        // Mock Static Utils for Signature Verification
        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.verifyPaymentSignature(any(JSONObject.class), anyString()))
                    .thenReturn(true);

            boolean result = paymentService.processPaymentForCompletion(request, sampleUser);

            assertTrue(result, "Should return true on successful verification and capture");

            verify(paymentRepo, atLeastOnce()).save(argThat(p -> p.getStatus() == PaymentStatus.SUCCESS));
            verify(subscriptionService).upgradeSubscription(eq(sampleUser), any(Payment.class));
            verify(publisher).publishEvent(any(PaymentSuccessfulEvent.class));
        }
    }

    //  Verifies that order creation fails if user tries to buy a cheaper/same plan
    @Test
    void initializePayment_ShouldThrowException_OnDowngrade() {
        sampleUser.getSubscription().setSubscriptionType(SubscriptionTypes.DIAMOND);

        assertThrows(SubscriptionDowngradeException.class, () ->
                paymentService.initializePayment(sampleUser, "GOLD"));
    }

    //  Verifies successful order initialization and local record creation
    @Test
    void initializePayment_ShouldCreateOrder_WhenValid() throws Exception {
        com.razorpay.Order mockOrder = mock(Order.class);
        when(mockOrder.get("id")).thenReturn("rzp_order_id");
        when(orderClient.create(any(JSONObject.class))).thenReturn(mockOrder);

        RazorpayOrderResponse response = paymentService.initializePayment(sampleUser, "GOLD");

        assertNotNull(response);
        assertEquals("rzp_order_id", response.getOrderId());
        verify(paymentRepo, times(1)).save(any(Payment.class));
    }

    //  Verifies that signature mismatch returns false and doesn't complete payment
    @Test
    void processPaymentForCompletion_ShouldReturnFalse_OnInvalidSignature() {
        PaymentVerificationRequest request = new PaymentVerificationRequest();
        request.setRazorpay_payment_id("pay_1");
        request.setRazorpay_signature("wrong_sig");
        request.setRazorpay_order_id("ord_1");

        when(paymentRepo.findByOrderId(anyString())).thenReturn(Optional.of(samplePayment));

        // Mocking static Razorpay Utils via Mockito
        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.verifyPaymentSignature(any(JSONObject.class), anyString())).thenReturn(false);

            boolean result = paymentService.processPaymentForCompletion(request, sampleUser);

            assertFalse(result);
            verify(paymentRepo, never()).deleteByUserIdAndStatusAndIdNot(any(), any(), any());
        }
    }

    // Verifies business logic: Upgrading subscription and sending email on success
    @Test
    void completePayment_ShouldSyncSubscriptionAndPublishEvent() {
        when(paymentRepo.findByOrderId("order_123")).thenReturn(Optional.of(samplePayment));

        paymentService.completePayment("order_123", "pay_id", true, LocalDateTime.now(),
                sampleUser, "GOLD", new BigDecimal("499"), "card");

        verify(subscriptionService).upgradeSubscription(eq(sampleUser), any(Payment.class));
        verify(publisher).publishEvent(any(PaymentSuccessfulEvent.class));
        verify(paymentRepo).save(argThat(p -> p.getStatus() == PaymentStatus.SUCCESS));
    }

    //  Verifies data integrity: Throwing error if success payment is missing date
    @Test
    void getLastPayment_ShouldThrowException_WhenDateIsMissing() {
        samplePayment.setStatus(PaymentStatus.SUCCESS);
        samplePayment.setPaymentDate(null);
        when(paymentRepo.findFirstByUserIdAndStatusOrderByCreatedAtDesc(anyLong(), any())).thenReturn(Optional.of(samplePayment));

        assertThrows(IllegalStateException.class, () -> paymentService.getLastPayment(sampleUser));
    }

    // Webhook: Verifies successful handling of payment.authorized event
    @Test
    void processRazorpayWebhook_ShouldCaptureAndComplete_OnAuthorizedEvent() throws Exception {
        String payload = "{ \"event\": \"payment.authorized\", \"payload\": { \"payment\": { \"entity\": { \"id\": \"pay_web_123\", \"order_id\": \"order_123\", \"amount\": 4900 } } } }";
        String signature = "valid_sig";

        when(paymentRepo.findByOrderId("order_123")).thenReturn(Optional.of(samplePayment));
        when(userService.findUserByUserId(anyLong())).thenReturn(sampleUser);
        when(subscriptionService.getPlanByAmount(any(BigDecimal.class))).thenReturn(SubscriptionTypes.GOLD);

        com.razorpay.Payment mockCaptured = mock(com.razorpay.Payment.class);
        when(mockCaptured.get("method")).thenReturn("upi");
        when(paymentClient.capture(anyString(), any(JSONObject.class))).thenReturn(mockCaptured);

        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.verifyWebhookSignature(any(), any(), any())).thenReturn(true);

            paymentService.processRazorpayWebhook(payload, signature);

            // Verify capture was called and payment saved as SUCCESS
            verify(paymentClient).capture(eq("pay_web_123"), any(JSONObject.class));
            verify(paymentRepo).save(argThat(p -> p.getStatus() == PaymentStatus.SUCCESS));
        }
    }

    // Webhook: Verifies refund event is published if capture fails inside webhook
    @Test
    void processRazorpayWebhook_ShouldPublishRefundEvent_OnCaptureFailure() throws Exception {
        String payload = "{ \"event\": \"payment.authorized\", \"payload\": { \"payment\": { \"entity\": { \"id\": \"pay_fail_123\", \"order_id\": \"order_123\", \"amount\": 4900 } } } }";

        when(paymentRepo.findByOrderId("order_123")).thenReturn(Optional.of(samplePayment));
        when(userService.findUserByUserId(anyLong())).thenReturn(sampleUser);
        when(subscriptionService.getPlanByAmount(any(BigDecimal.class))).thenReturn(SubscriptionTypes.GOLD);
        when(paymentClient.capture(anyString(), any(JSONObject.class))).thenThrow(new RuntimeException("Capture Failed"));

        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.verifyWebhookSignature(any(), any(), any())).thenReturn(true);

            paymentService.processRazorpayWebhook(payload, "sig");

            verify(publisher).publishEvent(any(PaymentRefundEvent.class));
            verify(paymentRepo).save(argThat(p -> p.getStatus() == PaymentStatus.FAILED));
        }
    }

    // CRON Logic: Verifies that stuck payments are captured if Razorpay shows 'authorized'
    @Test
    void reconcileStuckPayments_ShouldCaptureAuthorizedPayments() throws Exception {
        samplePayment.setStatus(PaymentStatus.PENDING);
        samplePayment.setUserId(1L);
        when(paymentRepo.findAllByStatusAndCreatedAtBefore(any(), any())).thenReturn(List.of(samplePayment));
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(sampleUser));

        com.razorpay.Payment rzpPayment = mock(com.razorpay.Payment.class);
        when(rzpPayment.get("status")).thenReturn("authorized");
        when(rzpPayment.get("id")).thenReturn("pay_captured_123");
        when(rzpPayment.get("method")).thenReturn("upi");

        when(orderClient.fetchPayments(anyString())).thenReturn(List.of(rzpPayment));
        when(paymentClient.capture(anyString(), any(JSONObject.class))).thenReturn(rzpPayment);
        when(paymentRepo.findByOrderId(anyString())).thenReturn(Optional.of(samplePayment));

        paymentService.reconcileStuckPayments();

        // Verify it triggers completion
        verify(subscriptionService).upgradeSubscription(eq(sampleUser), any(Payment.class));
        verify(paymentRepo).save(argThat(p -> p.getPaymentId().equals("pay_captured_123")));
    }

    // Cleanup Cron: Verifies junk payments are deleted correctly
    @Test
    void cleanJunk_ShouldInvokeRepositoryDelete() {
        paymentService.cleanJunk();

        // Verifying that delete is called for both PENDING and FAILED statuses
        verify(paymentRepo, times(1)).deleteByStatusAndCreatedAtBefore(eq(PaymentStatus.PENDING), any());
        verify(paymentRepo, times(1)).deleteByStatusAndCreatedAtBefore(eq(PaymentStatus.FAILED), any());
    }
}