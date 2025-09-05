package com.LuckyHub.Backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceImplTest {

    @Autowired
    EmailService service;

    @Test
    void sendEmail() {
        service.sendEmail(
                "insaneSoul620@gmail.com",
                "Welcome",
                "hi , This is an testing mail !"
        );
    }
}