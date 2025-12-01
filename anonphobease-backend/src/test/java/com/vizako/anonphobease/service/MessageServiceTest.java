package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Message;
import com.vizako.anonphobease.repository.MessageRepository;
import com.vizako.anonphobease.v1.dto.MessageDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void findAll_returnsMappedDtos() {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        ObjectId user1 = new ObjectId();
        ObjectId user2 = new ObjectId();
        ObjectId chat1 = new ObjectId();
        ObjectId chat2 = new ObjectId();

        Message m1 = new Message();
        m1.setId(id1);
        m1.setUserId(user1);
        m1.setChatId(chat1);
        m1.setContent("Hello");
        m1.setCreatedAt(new Date());

        Message m2 = new Message();
        m2.setId(id2);
        m2.setUserId(user2);
        m2.setChatId(chat2);
        m2.setContent("World");
        m2.setCreatedAt(new Date());

        when(messageRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MessageDTO> result = messageService.findAll();

        assertEquals(2, result.size());

        MessageDTO dto1 = result.get(0);
        MessageDTO dto2 = result.get(1);

        assertEquals(id1.toHexString(), dto1.getMessageId());
        assertEquals(user1.toHexString(), dto1.getUserId());
        assertEquals(chat1.toHexString(), dto1.getChatId());
        assertEquals("Hello", dto1.getContent());

        assertEquals(id2.toHexString(), dto2.getMessageId());
        assertEquals(user2.toHexString(), dto2.getUserId());
        assertEquals(chat2.toHexString(), dto2.getChatId());
        assertEquals("World", dto2.getContent());
    }

    @Test
    void findById_returnsDtoWhenFound() {
        ObjectId id = new ObjectId();
        ObjectId userId = new ObjectId();
        ObjectId chatId = new ObjectId();

        Message m = new Message();
        m.setId(id);
        m.setUserId(userId);
        m.setChatId(chatId);
        m.setContent("Test message");

        when(messageRepository.findById(id)).thenReturn(Optional.of(m));

        Optional<MessageDTO> result = messageService.findById(id.toHexString());

        assertTrue(result.isPresent());
        MessageDTO dto = result.get();
        assertEquals(id.toHexString(), dto.getMessageId());
        assertEquals(userId.toHexString(), dto.getUserId());
        assertEquals(chatId.toHexString(), dto.getChatId());
        assertEquals("Test message", dto.getContent());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        ObjectId id = new ObjectId();

        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        Optional<MessageDTO> result = messageService.findById(id.toHexString());

        assertTrue(result.isEmpty());
    }

    @Test
    void save_savesMessageAndReturnsDto() {
        ObjectId userId = new ObjectId();
        ObjectId chatId = new ObjectId();
        ObjectId savedId = new ObjectId();

        MessageDTO dto = new MessageDTO();
        dto.setMessageId(null);
        dto.setUserId(userId.toHexString());
        dto.setChatId(chatId.toHexString());
        dto.setContent("New message");

        Message savedEntity = new Message();
        savedEntity.setId(savedId);
        savedEntity.setUserId(userId);
        savedEntity.setChatId(chatId);
        savedEntity.setContent("New message");

        when(messageRepository.save(any(Message.class))).thenReturn(savedEntity);

        MessageDTO result = messageService.save(dto);

        assertEquals(savedId.toHexString(), result.getMessageId());
        assertEquals(userId.toHexString(), result.getUserId());
        assertEquals(chatId.toHexString(), result.getChatId());
        assertEquals("New message", result.getContent());
    }

    @Test
    void update_whenMessageExists_updatesAndReturnsDto() {
        ObjectId id = new ObjectId();
        ObjectId userId = new ObjectId();
        ObjectId chatId = new ObjectId();

        String idStr = id.toHexString();

        Message existing = new Message();
        existing.setId(id);
        existing.setUserId(userId);
        existing.setChatId(chatId);
        existing.setContent("Old content");

        MessageDTO dto = new MessageDTO();
        dto.setMessageId(idStr);
        dto.setUserId(userId.toHexString());
        dto.setChatId(chatId.toHexString());
        dto.setContent("Updated content");

        Message savedAfterUpdate = new Message();
        savedAfterUpdate.setId(id);
        savedAfterUpdate.setUserId(userId);
        savedAfterUpdate.setChatId(chatId);
        savedAfterUpdate.setContent("Updated content");

        when(messageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(messageRepository.save(any(Message.class))).thenReturn(savedAfterUpdate);

        Optional<MessageDTO> result = messageService.update(idStr, dto);

        assertTrue(result.isPresent());
        MessageDTO updated = result.get();
        assertEquals(idStr, updated.getMessageId());
        assertEquals("Updated content", updated.getContent());
    }

    @Test
    void update_whenDtoIsNull_returnsEmptyAndDoesNothing() {
        String idStr = new ObjectId().toHexString();

        Optional<MessageDTO> result = messageService.update(idStr, null);

        assertTrue(result.isEmpty());
        verify(messageRepository, never()).findById(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void update_whenMessageNotFound_returnsEmpty() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();

        MessageDTO dto = new MessageDTO();
        dto.setMessageId(idStr);
        dto.setContent("Updated content");

        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        Optional<MessageDTO> result = messageService.update(idStr, dto);

        assertTrue(result.isEmpty());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void deleteById_callsRepositoryDelete() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();

        messageService.deleteById(idStr);

        ArgumentCaptor<ObjectId> captor = ArgumentCaptor.forClass(ObjectId.class);
        verify(messageRepository).deleteById(captor.capture());

        assertEquals(idStr, captor.getValue().toHexString());
    }
}
