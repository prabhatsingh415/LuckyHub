package com.LuckyHub.Backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VideoServiceImplTest {

    @Autowired
    private VideoServiceImpl videoService;

    @Test
    void verifySameUser() {
        List<String> urlsSameChannel = List.of(
                "https://youtu.be/iCKu-eilSso?si=-QarQP-AYt9qhEDW",
                "https://youtu.be/0l84cgXqyIs?si=ZqqLuWTEuwT-Gens"
        );

        List<String> urlsDifferentChannel = List.of(
                "https://youtu.be/6mhWgMd62rs?si=dGdEUjftK05NuOKp",
                "https://youtu.be/0l84cgXqyIs?si=ZqqLuWTEuwT-Gens"
        );

        boolean sameChannel = videoService.verifySameUser(urlsSameChannel);
        System.out.println("Same channel test passed? " + sameChannel);
        assertTrue(sameChannel);

        try {
            videoService.verifySameUser(urlsDifferentChannel);
        } catch (com.LuckyHub.Backend.exception.VideosFromDifferentChannelsException e) {
            System.out.println("Different channels test threw exception as expected: " + e.getMessage());
        }
    }

}
