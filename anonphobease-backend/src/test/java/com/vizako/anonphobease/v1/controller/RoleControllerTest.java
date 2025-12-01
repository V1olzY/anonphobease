package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.RoleService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false) // отключаем security-фильтры, чтобы не было 403
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;


    private RoleDTO createSampleRoleDto() {
        RoleDTO dto = new RoleDTO();
        dto.setId("666666666666666666666666");
        dto.setName("ADMIN");
        return dto;
    }

    @Test
    @DisplayName("GET /v1/roles returns list of roles")
    void getAll_ReturnsList() throws Exception {
        List<RoleDTO> list = Arrays.asList(createSampleRoleDto());
        Mockito.when(roleService.findAll()).thenReturn(list);

        mockMvc.perform(get("/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("666666666666666666666666"))
                .andExpect(jsonPath("$[0].name").value("ADMIN"));

        verify(roleService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /v1/roles/{id} returns role when found")
    void getById_Found() throws Exception {
        RoleDTO dto = createSampleRoleDto();
        Mockito.when(roleService.findById("666666666666666666666666"))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/roles/{id}", "666666666666666666666666"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService, times(1)).findById("666666666666666666666666");
    }

    @Test
    @DisplayName("GET /v1/roles/{id} returns empty body when not found")
    void getById_NotFound_ReturnsNullBody() throws Exception {
        Mockito.when(roleService.findById("not-exists"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/roles/{id}", "not-exists"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(roleService, times(1)).findById("not-exists");
    }

    @Test
    @DisplayName("POST /v1/roles creates new role successfully")
    void create_Success() throws Exception {
        String adminId = "admin-123";
        RoleDTO input = new RoleDTO();
        input.setName("ADMIN");

        RoleDTO saved = createSampleRoleDto();
        Mockito.when(roleService.save(any(RoleDTO.class), eq(adminId)))
                .thenReturn(saved);

        mockMvc.perform(post("/v1/roles")
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService, times(1))
                .save(any(RoleDTO.class), eq(adminId));
    }


    @Test
    @DisplayName("PUT /v1/roles/{id} updates role successfully")
    void update_Success() throws Exception {
        String id = "666666666666666666666666";
        String adminId = "admin-123";

        RoleDTO updateDto = createSampleRoleDto();
        updateDto.setName("MODERATOR");

        Mockito.when(roleService.update(eq(id), any(RoleDTO.class), eq(adminId)))
                .thenReturn(Optional.of(updateDto));

        mockMvc.perform(put("/v1/roles/{id}", id)
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MODERATOR"));

        verify(roleService, times(1))
                .update(eq(id), any(RoleDTO.class), eq(adminId));
    }

    @Test
    @DisplayName("PUT /v1/roles/{id} returns 404 when role not found")
    void update_NotFound_Returns404() throws Exception {
        String id = "not-exists";
        String adminId = "admin-123";

        RoleDTO dto = createSampleRoleDto();

        Mockito.when(roleService.update(eq(id), any(RoleDTO.class), eq(adminId)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/roles/{id}", id)
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Role not found"));

        verify(roleService, times(1))
                .update(eq(id), any(RoleDTO.class), eq(adminId));
    }

    @Test
    @DisplayName("DELETE /v1/roles/{id} deletes role successfully")
    void delete_Success() throws Exception {
        String id = "666666666666666666666666";
        String adminId = "admin-123";

        mockMvc.perform(delete("/v1/roles/{id}", id)
                        .requestAttr("adminId", adminId))
                .andExpect(status().isNoContent());

        verify(roleService, times(1))
                .deleteById(id, adminId);
    }
}
