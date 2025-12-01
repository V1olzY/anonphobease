package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.LanguageService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.LanguageDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = LanguageController.class
)
@AutoConfigureMockMvc(addFilters = false)
class LanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LanguageService languageService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    private LanguageDTO createLanguageDto(String id) {
        return new LanguageDTO(id, "English", "en");
    }


    @Test
    @DisplayName("GET /v1/languages returns list of all languages")
    void getAll_returnsList() throws Exception {
        LanguageDTO lang1 = createLanguageDto(new ObjectId().toHexString());
        LanguageDTO lang2 = createLanguageDto(new ObjectId().toHexString());

        when(languageService.findAll()).thenReturn(List.of(lang1, lang2));

        mockMvc.perform(get("/v1/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(languageService).findAll();
    }


    @Test
    @DisplayName("GET /v1/languages/{id} returns language if found")
    void getById_returnsLanguage() throws Exception {
        String id = new ObjectId().toHexString();
        LanguageDTO dto = createLanguageDto(id);

        when(languageService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/languages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        verify(languageService).findById(id);
    }

    @Test
    @DisplayName("GET /v1/languages/{id} возвращает null если не найдено")
    void getById_notFound_returnsNull() throws Exception {
        String id = new ObjectId().toHexString();

        when(languageService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/languages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(languageService).findById(id);
    }


    @Test
    @DisplayName("POST /v1/languages creates a new language using adminId")
    void create_createsLanguage() throws Exception {
        String adminId = "admin-123";

        LanguageDTO requestDto = createLanguageDto(null);
        LanguageDTO savedDto = createLanguageDto(new ObjectId().toHexString());

        when(languageService.save(ArgumentMatchers.any(LanguageDTO.class), eq(adminId)))
                .thenReturn(savedDto);

        mockMvc.perform(post("/v1/languages")
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedDto.getId()));

        verify(languageService).save(any(LanguageDTO.class), eq(adminId));
    }


    @Test
    @DisplayName("PUT /v1/languages/{id} updates existing language using adminId")
    void update_existing_returnsUpdated() throws Exception {
        String id = new ObjectId().toHexString();
        String adminId = "admin-456";

        LanguageDTO requestDto = createLanguageDto(null);
        LanguageDTO updatedDto = createLanguageDto(id);

        when(languageService.update(eq(id), any(LanguageDTO.class), eq(adminId)))
                .thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/v1/languages/{id}", id)
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        verify(languageService).update(eq(id), any(LanguageDTO.class), eq(adminId));
    }

    @Test
    @DisplayName("PUT /v1/languages/{id} returns 404 if language not found")
    void update_notExisting_returns404() throws Exception {
        String id = new ObjectId().toHexString();
        String adminId = "admin-999";

        LanguageDTO requestDto = createLanguageDto(null);

        when(languageService.update(eq(id), any(LanguageDTO.class), eq(adminId)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/languages/{id}", id)
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isNotFound());

        verify(languageService).update(eq(id), any(LanguageDTO.class), eq(adminId));
    }


    @Test
    @DisplayName("DELETE /v1/languages/{id} deletes language using adminId")
    void delete_callsService() throws Exception {
        String id = new ObjectId().toHexString();
        String adminId = "admin-111";

        doNothing().when(languageService).deleteById(id, adminId);

        mockMvc.perform(delete("/v1/languages/{id}", id)
                        .requestAttr("adminId", adminId)
                )
                .andExpect(status().isNoContent());

        verify(languageService).deleteById(id, adminId);
    }
}
