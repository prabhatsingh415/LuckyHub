package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.service.JWTService;
import com.LuckyHub.Backend.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsService userDetails;

    @Test
    @WithMockUser
    void handleRazorpayWebhook_ShouldReturnOk() throws Exception {
        String mockPayload = "{\"event\":\"payment.captured\"}";
        String mockSignature = "razor_sig_123";

        mockMvc.perform(post("/api/webhooks/razorpay")
                        .with(csrf())
                        .header("X-Razorpay-Signature", mockSignature)
                        .content(mockPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Received"));


        verify(paymentService).processRazorpayWebhook(mockPayload, mockSignature);
    }
}