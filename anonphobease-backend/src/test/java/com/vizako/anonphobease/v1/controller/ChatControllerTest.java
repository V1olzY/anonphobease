package com.vizako.anonphobease.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizako.anonphobease.config.MongoConfig;
import com.vizako.anonphobease.config.security.JwtAdminIdFilter;
import com.vizako.anonphobease.config.security.UserDetailsServiceImpl;
import com.vizako.anonphobease.service.ChatService;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.ChatDTO;
import com.vizako.anonphobease.v1.dto.ChatsResponseDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ChatController.class,
        excludeAutoConfiguration = {
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = MongoConfig.class // твой конфиг Mongo, выкидываем его из контекста
        )
)
@AutoConfigureMockMvc(addFilters = false) // не поднимаем security-фильтры
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtAdminIdFilter jwtAdminIdFilter;

    private ChatDTO createChatDto(String id) {
        return new ChatDTO(
                id,
                new ObjectId().toHexString(), "English",
                "eng",
                new ObjectId().toHexString(), "Social Phobia",
                "(EN) Social Phobia"
        );
    }


    @Test
    @DisplayName("GET /v1/chats returns ChatsResponseDTO")
    void getAllChatsWithFilters_returnsResponse() throws Exception {
        ChatDTO chat1 = createChatDto(new ObjectId().toHexString());
        ChatDTO chat2 = createChatDto(new ObjectId().toHexString());
        ChatsResponseDTO responseDTO = new ChatsResponseDTO(
                List.of(chat1, chat2),
                List.of(),   // phobias
                List.of()    // languages
        );

        when(chatService.getAllChatsWithFilters()).thenReturn(responseDTO);

        mockMvc.perform(get("/v1/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chats.length()").value(2));

        verify(chatService).getAllChatsWithFilters();
    }


    @Test
    @DisplayName("GET /v1/chats/{id} if chat exists returns ChatDTO")
    void getById_returnsChat() throws Exception {
        ObjectId chatId = new ObjectId();
        ChatDTO dto = createChatDto(chatId.toHexString());

        when(chatService.findById(chatId)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/v1/chats/{id}", chatId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(chatId.toHexString()));

        verify(chatService).findById(chatId);
    }

    @Test
    @DisplayName("GET /v1/chats/{id} if chat not found returns null")
    void getById_notFound_returnsNull() throws Exception {
        ObjectId chatId = new ObjectId();
        when(chatService.findById(chatId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/chats/{id}", chatId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // контроллер возвращает null

        verify(chatService).findById(chatId);
    }


    @Test
    @DisplayName("POST /v1/chats creates chat and returns saved ChatDTO")
    void create_createsChatWithAdminId() throws Exception {
        String adminId = "admin-123";
        ChatDTO requestDto = createChatDto(null);
        ChatDTO savedDto = createChatDto(new ObjectId().toHexString());

        when(chatService.save(ArgumentMatchers.any(ChatDTO.class), eq(adminId)))
                .thenReturn(savedDto);

        mockMvc.perform(post("/v1/chats")
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedDto.getId()));

        verify(chatService).save(ArgumentMatchers.any(ChatDTO.class), eq(adminId));
    }


    @Test
    @DisplayName("PUT /v1/chats/{id} updates existing chat and returns updated ChatDTO")
    void update_existingChat_returnsUpdated() throws Exception {
        String chatId = new ObjectId().toHexString();
        String adminId = "admin-456";

        ChatDTO requestDto = createChatDto(null);
        ChatDTO updatedDto = createChatDto(chatId);

        when(chatService.update(eq(chatId), ArgumentMatchers.any(ChatDTO.class), eq(adminId)))
                .thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/v1/chats/{id}", chatId)
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(chatId));

        verify(chatService).update(eq(chatId), ArgumentMatchers.any(ChatDTO.class), eq(adminId));
    }

    @Test
    @DisplayName("PUT /v1/chats/{id} for non-existing chat returns 404")
    void update_notExistingChat_returns404() throws Exception {
        String chatId = new ObjectId().toHexString();
        String adminId = "admin-789";

        ChatDTO requestDto = createChatDto(null);

        when(chatService.update(eq(chatId), ArgumentMatchers.any(ChatDTO.class), eq(adminId)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/chats/{id}", chatId)
                        .requestAttr("adminId", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        verify(chatService).update(eq(chatId), ArgumentMatchers.any(ChatDTO.class), eq(adminId));
    }


    @Test
    @DisplayName("DELETE /v1/chats/{id} deletes chat by id")
    void delete_callsServiceWithAdminId() throws Exception {
        String chatId = new ObjectId().toHexString();
        String adminId = "admin-999";

        doNothing().when(chatService).deleteById(chatId, adminId);

        mockMvc.perform(delete("/v1/chats/{id}", chatId)
                        .requestAttr("adminId", adminId))
                .andExpect(status().isNoContent());

        verify(chatService).deleteById(chatId, adminId);
    }
}
