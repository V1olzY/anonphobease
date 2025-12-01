package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.UserLogService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.UserLogDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LogController.class)
@AutoConfigureMockMvc(addFilters = false)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserLogService userLogService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    private UserLogDTO createLogDto(String userId) {
        UserLogDTO dto = new UserLogDTO();
        return dto;
    }


    @Test
    @DisplayName("GET /v1/logs returns all logs")
    void getAllLogs_returnsList() throws Exception {
        UserLogDTO log1 = createLogDto("user-1");
        UserLogDTO log2 = createLogDto("user-2");

        when(userLogService.findAll()).thenReturn(List.of(log1, log2));

        mockMvc.perform(get("/v1/logs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userLogService).findAll();
    }

    @Test
    @DisplayName("GET /v1/logs/{userId} returns logs for specific user")
    void getLogsByUserId_returnsListForUser() throws Exception {
        String userId = "user-123";

        UserLogDTO log1 = createLogDto(userId);
        UserLogDTO log2 = createLogDto(userId);

        when(userLogService.findByUserId(userId)).thenReturn(List.of(log1, log2));

        mockMvc.perform(get("/v1/logs/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userLogService).findByUserId(userId);
    }

    @Test
    @DisplayName("GET /v1/logs/{userId} returns empty list when no logs for user")
    void getLogsByUserId_returnsEmptyListWhenNoLogs() throws Exception {
        String userId = "user-no-logs";

        when(userLogService.findByUserId(userId)).thenReturn(List.of());

        mockMvc.perform(get("/v1/logs/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userLogService).findByUserId(userId);
    }
}
