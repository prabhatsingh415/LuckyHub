package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.PaymentNotFoundException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.LastPaymentModel;
import com.LuckyHub.Backend.model.PaymentStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
    public class PaymentServiceImpl implements PaymentService {

        private final PaymentRepository paymentRepo;
        private final UserService userService;

        public PaymentServiceImpl(PaymentRepository paymentRepo, @Lazy UserService userService) {
            this.paymentRepo = paymentRepo;
            this.userService = userService;
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
        public Payment completePayment(String orderId, String paymentId, boolean signatureVerified, LocalDateTime paymentDate) {
            Payment payment = paymentRepo.findByOrderId(orderId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment record not found " + orderId));

            payment.setPaymentId(paymentId);
            payment.setSignatureVerified(signatureVerified);
            payment.setPaymentDate(paymentDate);
            payment.setStatus(signatureVerified ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

            return paymentRepo.save(payment);
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
}

