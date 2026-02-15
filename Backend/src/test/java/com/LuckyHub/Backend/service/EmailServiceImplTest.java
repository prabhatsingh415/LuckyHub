package com.LuckyHub.Backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String to = "test@gmail.com";
    private final String subject = "Welcome to LuckyHub";
    private final String body = "Hi, welcome aboard!";
    private final String sender = "support@luckyhub.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "sender", sender);
    }

    // Verify correct message properties
    @Test
    void sendEmail_ShouldSetCorrectFieldsAndSend() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(to, subject, body);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(to, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals(sender, sentMessage.getFrom());
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    // Verify execution
    @Test
    void sendAsyncEmail_ShouldCallSenderSuccessfully() {
        emailService.sendAsyncEmail(to, subject, body);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    //  Verify logger catches exception in async method
    @Test
    void sendAsyncEmail_ShouldHandleExceptionGracefully() {
        doThrow(new RuntimeException("SMTP Server Down")).when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendAsyncEmail(to, subject, body));

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}