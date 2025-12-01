package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.MessageService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.MessageDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    private MessageDTO createMessageDto(String id) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(id);
        dto.setUserId(new ObjectId().toHexString());
        dto.setChatId(new ObjectId().toHexString());
        dto.setContent("Test message");
        return dto;
    }

    @Test
    @DisplayName("GET /v1/messages returns list of MessageDTO")
    void getAll_returnsList() throws Exception {
        MessageDTO m1 = createMessageDto(new ObjectId().toHexString());
        MessageDTO m2 = createMessageDto(new ObjectId().toHexString());

        when(messageService.findAll()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/v1/messages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(messageService).findAll();
    }


    @Test
    @DisplayName("GET /v1/messages/{id} for existing id returns MessageDTO")
    void getById_returnsMessage() throws Exception {
        String messageId = new ObjectId().toHexString();
        MessageDTO dto = createMessageDto(messageId);

        when(messageService.findById(messageId)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/messages/{id}", messageId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(messageId));

        verify(messageService).findById(messageId);
    }

    @Test
    @DisplayName("GET /v1/messages/{id} for non-existing id returns null body")
    void getById_notFound_returnsNullBody() throws Exception {
        String messageId = new ObjectId().toHexString();

        when(messageService.findById(messageId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/messages/{id}", messageId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(messageService).findById(messageId);
    }


    @Test
    @DisplayName("POST /v1/messages создаёт сообщение")
    void create_createsMessage() throws Exception {
        MessageDTO requestDto = createMessageDto(null);
        MessageDTO savedDto = createMessageDto(new ObjectId().toHexString());

        when(messageService.save(any(MessageDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(savedDto.getMessageId()));

        verify(messageService).save(any(MessageDTO.class));
    }


    @Test
    @DisplayName("PUT /v1/messages/{id} for existing message updates and returns it")
    void update_existing_returnsUpdated() throws Exception {
        String messageId = new ObjectId().toHexString();

        MessageDTO requestDto = createMessageDto(null);
        MessageDTO updatedDto = createMessageDto(messageId);

        when(messageService.update(eq(messageId), any(MessageDTO.class)))
                .thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/v1/messages/{id}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(messageId));

        verify(messageService).update(eq(messageId), any(MessageDTO.class));
    }

    @Test
    @DisplayName("PUT /v1/messages/{id} при несуществующем сообщении возвращает 404")
    void update_notExisting_returns404() throws Exception {
        String messageId = new ObjectId().toHexString();

        MessageDTO requestDto = createMessageDto(null);

        when(messageService.update(eq(messageId), any(MessageDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/messages/{id}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        verify(messageService).update(eq(messageId), any(MessageDTO.class));
    }


    @Test
    @DisplayName("DELETE /v1/messages/{id} вызывает MessageService.deleteById")
    void delete_callsService() throws Exception {
        String messageId = new ObjectId().toHexString();

        mockMvc.perform(delete("/v1/messages/{id}", messageId))
                .andExpect(status().isNoContent());

        verify(messageService).deleteById(messageId);
    }
}
