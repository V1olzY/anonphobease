package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.PhobiaService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.PhobiaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhobiaController.class)
@AutoConfigureMockMvc(addFilters = false)
class PhobiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhobiaService phobiaService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private PhobiaDTO createSampleDto() {
        PhobiaDTO dto = new PhobiaDTO();
        dto.setId("666666666666666666666666");
        dto.setName("Social phobia");
        dto.setDescription("Afraid of social interaction");
        return dto;
    }

    @Test
    @DisplayName("GET /v1/phobias returns list of phobias")
    void getAll_ReturnsList() throws Exception {
        List<PhobiaDTO> list = Arrays.asList(createSampleDto());
        Mockito.when(phobiaService.findAll()).thenReturn(list);

        mockMvc.perform(get("/v1/phobias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Social phobia"));

        verify(phobiaService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /v1/phobias/{id} returns phobia when found")
    void getById_Found() throws Exception {
        PhobiaDTO dto = createSampleDto();
        Mockito.when(phobiaService.findById("666666666666666666666666"))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/phobias/{id}", "666666666666666666666666"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.name").value("Social phobia"));

        verify(phobiaService, times(1)).findById("666666666666666666666666");
    }

    @Test
    @DisplayName("GET /v1/phobias/{id} returns null when not found")
    void getById_NotFound_ReturnsNullBody() throws Exception {
        Mockito.when(phobiaService.findById("not-exists"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/phobias/{id}", "not-exists"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(phobiaService, times(1)).findById("not-exists");
    }

    @Test
    @DisplayName("POST /v1/phobias - создает фобию с adminId из request")
    void create_ReturnsCreatedPhobia() throws Exception {
        PhobiaDTO dto = createSampleDto();
        String adminId = "admin-123";

        Mockito.when(phobiaService.save(any(PhobiaDTO.class), eq(adminId)))
                .thenReturn(dto);

        mockMvc.perform(post("/v1/phobias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("adminId", adminId)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.name").value("Social phobia"));

        verify(phobiaService, times(1)).save(any(PhobiaDTO.class), eq(adminId));
    }

    @Test
    @DisplayName("PUT /v1/phobias/{id} updates phobia successfully")
    void update_Success() throws Exception {
        String id = "666666666666666666666666";
        String adminId = "admin-123";

        PhobiaDTO dto = createSampleDto();
        dto.setName("Updated name");

        Mockito.when(phobiaService.update(eq(id), any(PhobiaDTO.class), eq(adminId)))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(put("/v1/phobias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("adminId", adminId)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated name"));

        verify(phobiaService, times(1))
                .update(eq(id), any(PhobiaDTO.class), eq(adminId));
    }

    @Test
    @DisplayName("PUT /v1/phobias/{id} returns 404 when phobia not found")
    void update_NotFound_Returns404() throws Exception {
        String id = "not-exists";
        String adminId = "admin-123";
        PhobiaDTO dto = createSampleDto();

        Mockito.when(phobiaService.update(eq(id), any(PhobiaDTO.class), eq(adminId)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/phobias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("adminId", adminId)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Phobia not found"));

        verify(phobiaService, times(1))
                .update(eq(id), any(PhobiaDTO.class), eq(adminId));
    }

    @Test
    @DisplayName("DELETE /v1/phobias/{id} deletes phobia successfully")
    void delete_Success() throws Exception {
        String id = "666666666666666666666666";
        String adminId = "admin-123";

        mockMvc.perform(delete("/v1/phobias/{id}", id)
                        .requestAttr("adminId", adminId))
                .andExpect(status().isNoContent());

        verify(phobiaService, times(1))
                .deleteById(id, adminId);
    }
}
