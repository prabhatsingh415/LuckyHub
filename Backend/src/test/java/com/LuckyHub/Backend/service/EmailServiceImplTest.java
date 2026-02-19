package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.MailType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String to = "test@gmail.com";
    private final String subject = "Welcome to LuckyHub";
    private final String body = "Hi, welcome aboard!";

    @BeforeEach
    void setUp() {
        String sender = "support@luckyhub.com";
        ReflectionTestUtils.setField(emailService, "senderEmail", sender);
        String apiKey = "test-brevo-api-key";
        ReflectionTestUtils.setField(emailService, "brevoApiKey", apiKey);
    }

    @Test
    void sendEmail_ShouldNotCrash() {
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, body, MailType.VERIFICATION));
    }

    @Test
    void generateHtmlTemplate_ShouldContainBrandName() {
        String html = ReflectionTestUtils.invokeMethod(emailService,
                "generateHtmlTemplate", "Title", "Message", MailType.PAYMENT_SUCCESS);

        assertNotNull(html);
        assertTrue(html.contains("LuckyHub"));
        assertTrue(html.contains("#22c55e"));
    }
}