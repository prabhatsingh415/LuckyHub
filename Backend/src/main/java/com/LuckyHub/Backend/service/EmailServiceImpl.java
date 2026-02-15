package com.LuckyHub.Backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;

    @Value("${EMAIL_USERNAME}")
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom(sender);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        javaMailSender.send(mailMessage);
        log.info("[EMAIL-SERVICE] email successfully sent to: {}", to);
    }

    @Override
    @Async
    public void sendAsyncEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setFrom(sender);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);

            javaMailSender.send(mailMessage);
            log.info("[EMAIL-SERVICE-Async] Async email successfully sent to: {}", to);
        } catch (Exception e) {
            log.error("[EMAIL-SERVICE-Async] Failed to send async email to: {}. Error: {}", to, e.getMessage(), e);
        }
    }

}
