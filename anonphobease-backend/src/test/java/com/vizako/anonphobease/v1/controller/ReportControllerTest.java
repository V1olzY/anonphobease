package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.ReportService;
import com.vizako.anonphobease.v1.dto.ReportActionRequestDTO;
import com.vizako.anonphobease.v1.dto.ReportDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    // ====== основной сервис ======
    @MockitoBean
    private ReportService reportService;


    private ReportDTO createSampleReportDto() {
        ReportDTO dto = new ReportDTO();
        dto.setId("666666666666666666666666");
        dto.setChatId("chat-1");
        dto.setChatName("Social Phobia Chat");
        dto.setReportedUserId("reported-user-1");
        dto.setReportedUsername("bad_user");
        dto.setReporterUserId("reporter-user-1");
        dto.setReporterUsername("good_user");
        dto.setModeratorId("mod-1");
        dto.setModeratorName("Moderator");
        dto.setMessageId("msg-1");
        dto.setMessageContent("Offensive message");
        dto.setReason("Insult");
        dto.setIsResolved(false);
        dto.setCreatedAt(new Date());
        dto.setResolvedAt(null);
        dto.setActionTaken(null);
        dto.setActionReason("No action yet");
        return dto;
    }

    private ReportActionRequestDTO createSampleActionRequest() {
        ReportActionRequestDTO dto = new ReportActionRequestDTO();
        dto.setModeratorId("mod-1");
        dto.setActionReason("No violation found");
        return dto;
    }


    @Test
    @DisplayName("GET /v1/reports returns list of reports")
    void getAll_ReturnsList() throws Exception {
        List<ReportDTO> list = Arrays.asList(createSampleReportDto());
        Mockito.when(reportService.findAll()).thenReturn(list);

        mockMvc.perform(get("/v1/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("666666666666666666666666"))
                .andExpect(jsonPath("$[0].reason").value("Insult"))
                .andExpect(jsonPath("$[0].chatId").value("chat-1"));

        verify(reportService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /v1/reports/{id} returns report when found")
    void getById_Found() throws Exception {
        ReportDTO dto = createSampleReportDto();
        Mockito.when(reportService.findById("666666666666666666666666"))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/reports/{id}", "666666666666666666666666"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.reason").value("Insult"))
                .andExpect(jsonPath("$.reportedUsername").value("bad_user"));

        verify(reportService, times(1)).findById("666666666666666666666666");
    }

    @Test
    @DisplayName("GET /v1/reports/{id} returns empty body when not found")
    void getById_NotFound_ReturnsNullBody() throws Exception {
        Mockito.when(reportService.findById("not-exists"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/reports/{id}", "not-exists"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(reportService, times(1)).findById("not-exists");
    }

    @Test
    @DisplayName("POST /v1/reports creates a new report and sets createdAt")
    void create_SetsCreatedAtAndSaves() throws Exception {
        ReportDTO input = new ReportDTO();
        input.setChatId("chat-1");
        input.setChatName("Social Phobia Chat");
        input.setReportedUserId("reported-user-1");
        input.setReportedUsername("bad_user");
        input.setReporterUserId("reporter-user-1");
        input.setReporterUsername("good_user");
        input.setMessageId("msg-1");
        input.setMessageContent("Offensive message");
        input.setReason("Insult");
        input.setIsResolved(false);
        input.setActionReason("No action yet");

        ReportDTO saved = createSampleReportDto();
        Mockito.when(reportService.save(any(ReportDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.reason").value("Insult"))
                .andExpect(jsonPath("$.chatId").value("chat-1"));

        ArgumentCaptor<ReportDTO> captor = ArgumentCaptor.forClass(ReportDTO.class);
        verify(reportService, times(1)).save(captor.capture());
        ReportDTO passedToService = captor.getValue();
        assertNotNull(passedToService.getCreatedAt(), "createdAt should be set by the controller");
    }


    @Test
    @DisplayName("PUT /v1/reports/{id} updates an existing report")
    void update_Success() throws Exception {
        String id = "666666666666666666666666";

        ReportDTO updateDto = createSampleReportDto();
        updateDto.setReason("Updated reason");
        updateDto.setIsResolved(true);

        Mockito.when(reportService.update(eq(id), any(ReportDTO.class)))
                .thenReturn(Optional.of(updateDto));

        mockMvc.perform(put("/v1/reports/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Updated reason"))
                .andExpect(jsonPath("$.isResolved").value(true));

        verify(reportService, times(1))
                .update(eq(id), any(ReportDTO.class));
    }

    @Test
    @DisplayName("PUT /v1/reports/{id} returns 404 when report not found")
    void update_NotFound_Returns404() throws Exception {
        String id = "not-exists";
        ReportDTO dto = createSampleReportDto();

        Mockito.when(reportService.update(eq(id), any(ReportDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/reports/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Report not found"));

        verify(reportService, times(1))
                .update(eq(id), any(ReportDTO.class));
    }


    @Test
    @DisplayName("DELETE /v1/reports/{id} deletes the report")
    void delete_Success() throws Exception {
        String id = "666666666666666666666666";

        mockMvc.perform(delete("/v1/reports/{id}", id))
                .andExpect(status().isNoContent());

        verify(reportService, times(1))
                .deleteById(id);
    }


    @Test
    @DisplayName("POST /v1/reports/{id}/no-violation marks report as no violation")
    void markNoViolation_Success() throws Exception {
        String id = "666666666666666666666666";
        ReportActionRequestDTO actionRequest = createSampleActionRequest();
        ReportDTO dto = createSampleReportDto();

        Mockito.when(reportService.markNoViolation(eq(id), any(ReportActionRequestDTO.class)))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(post("/v1/reports/{id}/no-violation", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.reason").value("Insult"));

        verify(reportService, times(1))
                .markNoViolation(eq(id), any(ReportActionRequestDTO.class));
    }

    @Test
    @DisplayName("POST /v1/reports/{id}/no-violation returns 404 when report not found")
    void markNoViolation_NotFound_Returns404() throws Exception {
        String id = "not-exists";
        ReportActionRequestDTO actionRequest = createSampleActionRequest();

        Mockito.when(reportService.markNoViolation(eq(id), any(ReportActionRequestDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/v1/reports/{id}/no-violation", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Report not found"));

        verify(reportService, times(1))
                .markNoViolation(eq(id), any(ReportActionRequestDTO.class));
    }


    @Test
    @DisplayName("POST /v1/reports/{id}/ban bans the reported user")
    void banReport_Success() throws Exception {
        String id = "666666666666666666666666";
        ReportActionRequestDTO actionRequest = createSampleActionRequest();
        ReportDTO dto = createSampleReportDto();

        Mockito.when(reportService.banReport(eq(id), any(ReportActionRequestDTO.class)))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(post("/v1/reports/{id}/ban", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.reportedUsername").value("bad_user"));

        verify(reportService, times(1))
                .banReport(eq(id), any(ReportActionRequestDTO.class));
    }

    @Test
    @DisplayName("POST /v1/reports/{id}/ban returns 404 when report not found")
    void banReport_NotFound_Returns404() throws Exception {
        String id = "not-exists";
        ReportActionRequestDTO actionRequest = createSampleActionRequest();

        Mockito.when(reportService.banReport(eq(id), any(ReportActionRequestDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/v1/reports/{id}/ban", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Report not found"));

        verify(reportService, times(1))
                .banReport(eq(id), any(ReportActionRequestDTO.class));
    }
}
