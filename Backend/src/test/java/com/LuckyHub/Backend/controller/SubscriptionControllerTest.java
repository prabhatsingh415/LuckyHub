package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.model.*;
import com.LuckyHub.Backend.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private SubscriptionService subscriptionService;
    @MockitoBean private PaymentService paymentService;
    @MockitoBean private UserService userService;

    @MockitoBean private JWTService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    private final String testEmail = "test@luckyhub.com";

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setEmail(testEmail);
        when(userService.findUserByEmail(testEmail)).thenReturn(Optional.of(mockUser));
    }

    // Verifies Razorpay order initialization
    @Test
    @WithMockUser(username = testEmail)
    void proceedPayment_ShouldReturnOrderDetails() throws Exception {
        RazorpayOrderResponse mockResponse = RazorpayOrderResponse.builder().build();
        when(paymentService.initializePayment(any(User.class), eq("GOLD"))).thenReturn(mockResponse);

        mockMvc.perform(post("/subscription/createOrder")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"planName\":\"gold\"}"))
                        .andExpect(status().isOk());
    }

    // Verify Payment
    @Test
    @WithMockUser(username = testEmail)
    void verifyPayment_ShouldReturnSuccess_WhenVerified() throws Exception {
        when(paymentService.processAndVerify(any(User.class), any(PaymentVerificationRequest.class)))
                .thenReturn(true);

        mockMvc.perform(post("/subscription/verifyPayment")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"razorpay_order_id\":\"ord_123\", \"razorpay_payment_id\":\"pay_123\", \"razorpay_signature\":\"sig_123\"}"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("success"));
    }

    // Verify Payment (Pending/Fail)
    @Test
    @WithMockUser(username = testEmail)
    void verifyPayment_ShouldReturnPending_WhenNotImmediatelyVerified() throws Exception {
        when(paymentService.processAndVerify(any(User.class), any(PaymentVerificationRequest.class)))
                .thenReturn(false);

        mockMvc.perform(post("/subscription/verifyPayment")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"razorpay_order_id\":\"ord_123\", \"razorpay_payment_id\":\"pay_123\", \"razorpay_signature\":\"sig_123\"}"))
                        .andExpect(status().isAccepted()) // Check for 202 status
                        .andExpect(jsonPath("$.status").value("pending"));
    }

    // Get Subscription
    @Test
    @WithMockUser(username = testEmail)
    void getSubscription_ShouldReturnUserSub() throws Exception {
        mockMvc.perform(get("/subscription/getSubscription"))
                .andExpect(status().isOk());
    }
}