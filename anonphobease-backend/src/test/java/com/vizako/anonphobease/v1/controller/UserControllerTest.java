package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.UserService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.dto.UserDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    private UserDTO createSampleUserDto() {
        return UserDTO.builder()
                .id("666666666666666666666666")
                .username("testuser")
                .role(RoleDTO.builder()
                        .id("role123")
                        .name("ADMIN")
                        .build())
                .isActive(true)
                .createdAt(new Date())
                .build();
    }

    @Test
    @DisplayName("GET /v1/users returns list of users")
    void getAll_ReturnsList() throws Exception {
        List<UserDTO> list = Arrays.asList(createSampleUserDto());
        Mockito.when(userService.findAll()).thenReturn(list);

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("666666666666666666666666"))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].role.name").value("ADMIN"))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /v1/users/{id} returns user when found")
    void getById_Found() throws Exception {
        UserDTO dto = createSampleUserDto();
        Mockito.when(userService.findById("666666666666666666666666"))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/users/{id}", "666666666666666666666666"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role.name").value("ADMIN"));

        verify(userService, times(1)).findById("666666666666666666666666");
    }

    @Test
    @DisplayName("GET /v1/users/{id} returns null when not found")
    void getById_NotFound() throws Exception {
        Mockito.when(userService.findById("nope"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/users/{id}", "nope"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService, times(1)).findById("nope");
    }

    @Test
    @DisplayName("POST /v1/users creates a new user successfully")
    void create_Success() throws Exception {
        UserDTO input = UserDTO.builder()
                .username("newuser")
                .role(RoleDTO.builder().id("role1").name("MODERATOR").build())
                .isActive(true)
                .build();

        UserDTO saved = createSampleUserDto();

        Mockito.when(userService.save(any(UserDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("666666666666666666666666"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).save(any(UserDTO.class));
    }


    @Test
    @DisplayName("PUT /v1/users/{id} updates user successfully")
    void update_Success() throws Exception {
        String id = "666666666666666666666666";

        UserDTO updated = createSampleUserDto();
        updated.setUsername("updatedName");

        Mockito.when(userService.update(eq(id), any(UserDTO.class)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(put("/v1/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedName"));

        verify(userService, times(1))
                .update(eq(id), any(UserDTO.class));
    }

    @Test
    @DisplayName("PUT /v1/users/{id} returns null when user not found")
    void update_NotFound() throws Exception {
        String id = "not-found";
        UserDTO dto = createSampleUserDto();

        Mockito.when(userService.update(eq(id), any(UserDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService, times(1))
                .update(eq(id), any(UserDTO.class));
    }

    @Test
    @DisplayName("DELETE /v1/users/{id} deletes user successfully")
    void delete_Success() throws Exception {
        String id = "666666666666666666666666";

        mockMvc.perform(delete("/v1/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService, times(1))
                .deleteById(new ObjectId(id));
    }
}
