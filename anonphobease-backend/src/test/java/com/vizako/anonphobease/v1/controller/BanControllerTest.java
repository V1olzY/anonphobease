package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.BanService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.BanDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BanController.class)
@AutoConfigureMockMvc(addFilters = false)
class BanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BanService banService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    @Test
    void getAll_returnsListOfBans() throws Exception {
        BanDTO dto = new BanDTO();
        dto.setId(new ObjectId().toHexString());
        dto.setUserId(new ObjectId().toHexString());
        dto.setUsername("bannedUser");
        dto.setChatId(new ObjectId().toHexString());
        dto.setChatName("(EN) Social phobia");
        dto.setBanReason("Reason");
        dto.setBannedAt(new Date());

        Mockito.when(banService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/bans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId())))
                .andExpect(jsonPath("$[0].username", is("bannedUser")))
                .andExpect(jsonPath("$[0].chatName", is("(EN) Social phobia")))
                .andExpect(jsonPath("$[0].banReason", is("Reason")));

        Mockito.verify(banService).findAll();
    }

    @Test
    void getById_whenBanExists_returnsBan() throws Exception {
        String id = new ObjectId().toHexString();
        BanDTO dto = new BanDTO();
        dto.setId(id);
        dto.setUserId(new ObjectId().toHexString());
        dto.setBanReason("Reason");

        Mockito.when(banService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/bans/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.banReason", is("Reason")));

        Mockito.verify(banService).findById(id);
    }

    @Test
    void getById_whenBanNotFound_returns404() throws Exception {
        String id = new ObjectId().toHexString();
        Mockito.when(banService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/bans/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(banService).findById(id);
    }

    @Test
    void create_createsBanAndReturnsDto() throws Exception {
        BanDTO request = new BanDTO();
        request.setUserId(new ObjectId().toHexString());
        request.setChatId(new ObjectId().toHexString());
        request.setModeratorId(new ObjectId().toHexString());
        request.setBanReason("Reason");

        BanDTO saved = new BanDTO();
        saved.setId(new ObjectId().toHexString());
        saved.setUserId(request.getUserId());
        saved.setChatId(request.getChatId());
        saved.setModeratorId(request.getModeratorId());
        saved.setBanReason("Reason");

        Mockito.when(banService.save(Mockito.any(BanDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/v1/bans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId())))
                .andExpect(jsonPath("$.banReason", is("Reason")));

        Mockito.verify(banService).save(Mockito.any(BanDTO.class));
    }

    @Test
    void update_whenBanExists_returnsUpdatedDto() throws Exception {
        String id = new ObjectId().toHexString();

        BanDTO request = new BanDTO();
        request.setUserId(new ObjectId().toHexString());
        request.setChatId(new ObjectId().toHexString());
        request.setModeratorId(new ObjectId().toHexString());
        request.setBanReason("Updated reason");

        BanDTO updated = new BanDTO();
        updated.setId(id);
        updated.setUserId(request.getUserId());
        updated.setChatId(request.getChatId());
        updated.setModeratorId(request.getModeratorId());
        updated.setBanReason("Updated reason");

        Mockito.when(banService.update(eq(id), Mockito.any(BanDTO.class)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(put("/v1/bans/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.banReason", is("Updated reason")));

        Mockito.verify(banService).update(eq(id), Mockito.any(BanDTO.class));
    }

    @Test
    void update_whenBanNotFound_returns404() throws Exception {
        String id = new ObjectId().toHexString();

        BanDTO request = new BanDTO();
        request.setUserId(new ObjectId().toHexString());
        request.setChatId(new ObjectId().toHexString());
        request.setModeratorId(new ObjectId().toHexString());
        request.setBanReason("Updated reason");

        Mockito.when(banService.update(eq(id), Mockito.any(BanDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/bans/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        Mockito.verify(banService).update(eq(id), Mockito.any(BanDTO.class));
    }

    @Test
    void delete_deletesBanAndReturns204() throws Exception {
        String id = new ObjectId().toHexString();

        mockMvc.perform(delete("/v1/bans/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(banService).deleteById(id);
    }
}
