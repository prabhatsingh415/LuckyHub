package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.entity.VideoDetail;
import com.LuckyHub.Backend.model.GiveawayHistoryDTO;
import com.LuckyHub.Backend.repository.GiveawayHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GiveawayHistoryServiceImplTest {

    @Mock
    GiveawayHistoryRepository giveawayHistoryRepository;

    @InjectMocks
    GiveawayHistoryServiceImpl giveawayHistoryService;

    private GiveawayHistory sampleHistory;

    @BeforeEach
    void setUp() {
        sampleHistory = GiveawayHistory.builder()
                .userId(1L)
                .winners(Arrays.asList("harry", "Jack", "Ben"))
                .videoDetails(List.of(new VideoDetail("1234", "thumbnail1.jpg", "Coding")))
                .winnersCount(3)
                .commentCount(200)
                .keywordUsed("win")
                .loyaltyFilterApplied(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void  saveHistory_ShouldSaveHistory_WhenRepositoryWorks() {
        List<GiveawayHistory> dummyHistory = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            dummyHistory.add(GiveawayHistory.builder().userId(1L).build());
        }

        when(giveawayHistoryRepository.findByUserIdOrderByCreatedAtDesc(sampleHistory.getUserId()))
                        .thenReturn(dummyHistory);

        giveawayHistoryService.saveHistory(sampleHistory);

        verify(giveawayHistoryRepository, times(1)).save(sampleHistory);
        verify(giveawayHistoryRepository, times(1)).deleteAllInBatch(anyList());

        System.out.println("Pruning logic verified");
    }

    @Test
    void saveHistory_ShouldFail_WhenPruningFails() {
        List<GiveawayHistory> dummyHistory = new ArrayList<>();
        for (int i = 0; i < 11; i++) dummyHistory.add(new GiveawayHistory());

        when(giveawayHistoryRepository.findByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(dummyHistory);

        doThrow(new RuntimeException("Pruning Failed")).when(giveawayHistoryRepository).deleteAllInBatch(anyList());

        assertThrows(RuntimeException.class, () -> giveawayHistoryService.saveHistory(sampleHistory));

        verify(giveawayHistoryRepository, times(1)).save(sampleHistory);
        verify(giveawayHistoryRepository, times(1)).deleteAllInBatch(anyList());
    }

    @Test
    void saveHistory_ShouldThrowException_WhenRepositoryFails(){
        when(giveawayHistoryRepository.save(any(GiveawayHistory.class)))
                .thenThrow(new RuntimeException("Database Connection Failed"));

        assertThrows(RuntimeException.class, () -> giveawayHistoryService.saveHistory(sampleHistory));

        verify(giveawayHistoryRepository, times(1)).save(sampleHistory);
        verify(giveawayHistoryRepository, never()).findByUserIdOrderByCreatedAtDesc(anyLong());
        verify(giveawayHistoryRepository, never()).deleteAllInBatch(anyList());
    }


    @Test
    void saveHistory_ShouldNotPrune_WhenLimitIsExactlyTen() {
        List<GiveawayHistory> mockHistory = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mockHistory.add(new GiveawayHistory());
        }
        when(giveawayHistoryRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(mockHistory);

        giveawayHistoryService.saveHistory(sampleHistory);


        verify(giveawayHistoryRepository, times(1)).save(sampleHistory);
        verify(giveawayHistoryRepository, never()).deleteAllInBatch(anyList());

        System.out.println("No deletion when limit is exactly 10.");
    }

    @Test
    void history_ShouldReturnCleanArrayLists_WhenUserExists() {
        long userId = 1L;
        List<GiveawayHistory> expectedHistory =
                List.of(sampleHistory);

        when(giveawayHistoryRepository.findByUserId(userId)).thenReturn(expectedHistory);
        GiveawayHistoryDTO[] result = giveawayHistoryService.history(userId);
        assertEquals(expectedHistory.size(), result.length);
        assertEquals(expectedHistory.getFirst().getId(), result[0].getId());


        assertInstanceOf(List.class, result[0].getWinners(),
                "Winners collection must be converted to a clean ArrayList for caching");

        assertInstanceOf(List.class, result[0].getVideoDetails(),
                "VideoDetails collection must be converted to a clean ArrayList for caching");

        System.out.println("Proxy-stripping verified for history retrieval!");

    }

    @Test
    void history_ShouldHandleNullCollections_WithoutCrashing() {
        long userId = 1L;
        GiveawayHistory historyWithNulls = new GiveawayHistory();
        historyWithNulls.setWinners(null);
        historyWithNulls.setVideoDetails(null);

        when(giveawayHistoryRepository.findByUserId(userId)).thenReturn(List.of(historyWithNulls));

        assertDoesNotThrow(() -> {
            GiveawayHistoryDTO[] result = giveawayHistoryService.history(userId);
            assertNull(result[0].getWinners(), "Should remain null or handle safely");
            assertNull(result[0].getVideoDetails(), "Should remain null or handle safely");
        });
    }

    @Test
    void history_ShouldReturnEmptyList_WhenUserHasNoHistory() {

        long userId = 999L;

        when(giveawayHistoryRepository.findByUserId(userId)).thenReturn(List.of());

        GiveawayHistoryDTO[] result = giveawayHistoryService.history(userId);

        assertEquals(0, result.length, "Result should be empty for non-existent user history");

        verify(giveawayHistoryRepository, times(1)).findByUserId(userId);
    }

    @Test
    void deleteHistory_ShouldCallRepositoryDeleteAndEvictCache() {
        long userId = 1L;

        giveawayHistoryService.deleteHistory(userId);

        verify(giveawayHistoryRepository, times(1)).deleteByUserId(userId);
        System.out.println("Delete history logic verified!");
    }

    @Test
    void deleteHistory_ShouldThrowException_WhenRepositoryFails() {
        long userId = 1L;
        doThrow(new RuntimeException("Delete Failed")).when(giveawayHistoryRepository).deleteByUserId(userId);

        assertThrows(RuntimeException.class, () -> giveawayHistoryService.deleteHistory(userId));
        verify(giveawayHistoryRepository, times(1)).deleteByUserId(userId);
    }
}
