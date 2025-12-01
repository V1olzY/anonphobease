package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Chat;
import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.repository.ChatRepository;
import com.vizako.anonphobease.v1.dto.ChatDTO;
import com.vizako.anonphobease.v1.dto.ChatsResponseDTO;
import com.vizako.anonphobease.v1.dto.LanguageDTO;
import com.vizako.anonphobease.v1.dto.PhobiaDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @MockitoBean
    private ChatRepository chatRepository;

    @MockitoBean
    private LanguageService languageService;

    @MockitoBean
    private PhobiaService phobiaService;

    @MockitoBean
    private UserLogService userLogService;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setup() {
        chatService.setUserLogService(userLogService);
    }


    @Test
    void findAll_returnsMappedChatDTOs() {
        ObjectId chatId = new ObjectId();
        ObjectId langId = new ObjectId();
        ObjectId phobiaId = new ObjectId();

        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setLanguageId(langId);
        chat.setPhobiaId(phobiaId);

        when(chatRepository.findAll()).thenReturn(List.of(chat));

        List<ChatDTO> result = chatService.findAll();

        assertEquals(1, result.size());
        ChatDTO dto = result.get(0);
        assertEquals(chatId.toHexString(), dto.getId());
        assertEquals(langId.toHexString(), dto.getLanguageId());
        assertEquals(phobiaId.toHexString(), dto.getPhobiaId());
    }

    @Test
    void getAllChatsWithFilters_returnsChatsWithLanguageAndPhobiaNames() {
        ObjectId chatId = new ObjectId();
        ObjectId langId = new ObjectId();
        ObjectId phobiaId = new ObjectId();

        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setLanguageId(langId);
        chat.setPhobiaId(phobiaId);

        LanguageDTO langDTO = new LanguageDTO();
        langDTO.setId(langId.toHexString());
        langDTO.setName("English");

        PhobiaDTO phobiaDTO = new PhobiaDTO();
        phobiaDTO.setId(phobiaId.toHexString());
        phobiaDTO.setName("Social phobia");

        when(chatRepository.findAll()).thenReturn(List.of(chat));
        when(languageService.findAll()).thenReturn(List.of(langDTO));
        when(phobiaService.findAll()).thenReturn(List.of(phobiaDTO));

        ChatsResponseDTO result = chatService.getAllChatsWithFilters();

        assertEquals(1, result.getChats().size());
        ChatDTO dto = result.getChats().get(0);

        assertEquals(chatId.toHexString(), dto.getId());
        assertEquals(langId.toHexString(), dto.getLanguageId());
        assertEquals("English", dto.getLanguageName());
        assertEquals(phobiaId.toHexString(), dto.getPhobiaId());
        assertEquals("Social phobia", dto.getPhobiaName());

        assertEquals(1, result.getLanguages().size());
        assertEquals(1, result.getPhobias().size());
    }

    @Test
    void findById_returnsChatDTOWhenExists() {
        ObjectId chatId = new ObjectId();
        ObjectId langId = new ObjectId();
        ObjectId phobiaId = new ObjectId();

        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setLanguageId(langId);
        chat.setPhobiaId(phobiaId);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        Optional<ChatDTO> result = chatService.findById(chatId);

        assertTrue(result.isPresent());
        ChatDTO dto = result.get();
        assertEquals(chatId.toHexString(), dto.getId());
        assertEquals(langId.toHexString(), dto.getLanguageId());
        assertEquals(phobiaId.toHexString(), dto.getPhobiaId());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        ObjectId chatId = new ObjectId();

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        Optional<ChatDTO> result = chatService.findById(chatId);

        assertTrue(result.isEmpty());
    }

    @Test
    void save_savesChatAndCreatesLog() {
        String userId = new ObjectId().toHexString();
        ObjectId chatId = new ObjectId();
        ObjectId langId = new ObjectId();
        ObjectId phobiaId = new ObjectId();

        ChatDTO dto = new ChatDTO();
        dto.setId(null);
        dto.setLanguageId(langId.toHexString());
        dto.setPhobiaId(phobiaId.toHexString());

        Chat savedEntity = new Chat();
        savedEntity.setId(chatId);
        savedEntity.setLanguageId(langId);
        savedEntity.setPhobiaId(phobiaId);


        when(chatRepository.save(any(Chat.class))).thenReturn(savedEntity);

        ChatDTO result = chatService.save(dto, userId);

        assertEquals(chatId.toHexString(), result.getId());
        assertEquals(langId.toHexString(), result.getLanguageId());
        assertEquals(phobiaId.toHexString(), result.getPhobiaId());


        verify(userLogService).save(
                eq(userId),
                eq(LogType.CHAT_SAVE),
                eq(chatId.toHexString()),
                eq(RelatedEntityType.CHAT)
        );
    }

    @Test
    void update_whenChatExists_updatesAndLogs() {
        ObjectId chatId = new ObjectId();
        ObjectId langId = new ObjectId();
        ObjectId phobiaId = new ObjectId();

        String chatIdStr = chatId.toHexString();
        String userId = new ObjectId().toHexString();

        Chat existing = new Chat();
        existing.setId(chatId);
        existing.setLanguageId(langId);
        existing.setPhobiaId(phobiaId);

        ChatDTO dto = new ChatDTO();
        dto.setId(chatIdStr);
        dto.setLanguageId(langId.toHexString());
        dto.setPhobiaId(phobiaId.toHexString());

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(existing));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ChatDTO> result = chatService.update(chatIdStr, dto, userId);

        assertTrue(result.isPresent());
        assertEquals(chatIdStr, result.get().getId());

        verify(userLogService).save(
                eq(userId),
                eq(LogType.CHAT_UPDATE),
                eq(chatIdStr),
                eq(RelatedEntityType.CHAT)
        );
    }

    @Test
    void update_whenChatNotFound_returnsEmptyAndDoesNotLog() {
        String chatIdStr = new ObjectId().toHexString();
        String userId = new ObjectId().toHexString();

        ChatDTO dto = new ChatDTO();
        dto.setId(chatIdStr);

        when(chatRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

        Optional<ChatDTO> result = chatService.update(chatIdStr, dto, userId);

        assertTrue(result.isEmpty());
        verify(userLogService, never()).save(
                anyString(),
                any(),
                anyString(),
                any()
        );
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void getChatName_returnsLanguageAndPhobiaName() {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setLanguageId(new ObjectId().toHexString());
        chatDTO.setPhobiaId(new ObjectId().toHexString());

        LanguageDTO langDTO = new LanguageDTO();
        langDTO.setId(chatDTO.getLanguageId());
        langDTO.setName("English");

        PhobiaDTO phobiaDTO = new PhobiaDTO();
        phobiaDTO.setId(chatDTO.getPhobiaId());
        phobiaDTO.setName("Social phobia");

        when(languageService.findById(chatDTO.getLanguageId())).thenReturn(Optional.of(langDTO));
        when(phobiaService.findById(chatDTO.getPhobiaId())).thenReturn(Optional.of(phobiaDTO));

        String name = chatService.getChatNameById(chatDTO.getId());

        assertEquals("English - Social phobia", name);
    }

    @Test
    void getChatName_usesDefaultsWhenLanguageOrPhobiaNotFound() {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setLanguageId(new ObjectId().toHexString());
        chatDTO.setPhobiaId(new ObjectId().toHexString());

        when(languageService.findById(chatDTO.getLanguageId())).thenReturn(Optional.empty());
        when(phobiaService.findById(chatDTO.getPhobiaId())).thenReturn(Optional.empty());

        String name = chatService.getChatNameById(chatDTO.getId());

        assertEquals("Unknown Language - Unknown Phobia", name);
    }

    @Test
    void getChatNameById_returnsChatNameWhenChatExists() {
        ObjectId chatId = new ObjectId();
        ObjectId langId = new ObjectId();
        ObjectId phobiaId = new ObjectId();

        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setLanguageId(langId);
        chat.setPhobiaId(phobiaId);

        LanguageDTO langDTO = new LanguageDTO();
        langDTO.setId(langId.toHexString());
        langDTO.setName("English");

        PhobiaDTO phobiaDTO = new PhobiaDTO();
        phobiaDTO.setId(phobiaId.toHexString());
        phobiaDTO.setName("Social phobia");

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(languageService.findById(langId.toHexString())).thenReturn(Optional.of(langDTO));
        when(phobiaService.findById(phobiaId.toHexString())).thenReturn(Optional.of(phobiaDTO));

        String result = chatService.getChatNameById(chatId.toHexString());

        assertEquals("English - Social phobia", result);
    }

    @Test
    void getChatNameById_returnsUnknownChatWhenNotFound() {
        String chatIdStr = new ObjectId().toHexString();

        when(chatRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

        String result = chatService.getChatNameById(chatIdStr);

        assertEquals("Unknown Chat", result);
    }

    @Test
    void deleteById_deletesChatAndCreatesLog() {
        String chatIdStr = new ObjectId().toHexString();
        String userId = new ObjectId().toHexString();

        chatService.deleteById(chatIdStr, userId);


        verify(userLogService).save(
                eq(userId),
                eq(LogType.CHAT_DELETE),
                eq(chatIdStr),
                eq(RelatedEntityType.CHAT)
        );

        ArgumentCaptor<ObjectId> idCaptor = ArgumentCaptor.forClass(ObjectId.class);
        verify(chatRepository).deleteById(idCaptor.capture());

        assertEquals(chatIdStr, idCaptor.getValue().toHexString());
    }
}
