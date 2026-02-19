package com.LuckyHub.Backend.controller;

import com.LuckyHub.Backend.entity.GiveawayHistory;
import com.LuckyHub.Backend.model.GiveawayHistoryDTO;
import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;
import com.LuckyHub.Backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GiveawayController.class)
class GiveawayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private WinnerService winnerService;
    @MockitoBean private GiveawayHistoryService giveawayHistoryService;
    @MockitoBean private UserService userService;

    @MockitoBean private JWTService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    // Checks winner selection logic
    @Test
    @WithMockUser(username = "test@luckyhub.com")
    void getWinner_ShouldReturnWinnerResponse() throws Exception {
        WinnerResponse mockResponse = new WinnerResponse();
        when(winnerService.findWinner(any(WinnerRequest.class), anyString())).thenReturn(mockResponse);

        mockMvc.perform(post("/giveaway/pick-a-winner")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"videoUrl\":\"http://youtube.com/abc\", \"winnerCount\":1}"))
                        .andExpect(status().isOk());
    }

    // Verifies UserID extraction and History retrieval
    @Test
    @WithMockUser(username = "test@luckyhub.com")
    void getHistory_ShouldReturnList() throws Exception {
        when(userService.findUserIdByEmail("test@luckyhub.com")).thenReturn(1L);
        when(giveawayHistoryService.history(1L)).thenReturn(new GiveawayHistoryDTO[]{});

        mockMvc.perform(get("/giveaway/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}