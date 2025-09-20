package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Payment;
import com.LuckyHub.Backend.exception.PaymentNotFoundException;
import com.LuckyHub.Backend.model.PaymentStatus;
import com.LuckyHub.Backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
    public class PaymentServiceImpl implements PaymentService {

        private final PaymentRepository paymentRepo;

        public PaymentServiceImpl(PaymentRepository paymentRepo) {
            this.paymentRepo = paymentRepo;
        }

    @Override
        public Payment createPartialPayment(Long userId, String planType, BigDecimal amount, String currency, String orderId, String receiptId) {
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
    }

