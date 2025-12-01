package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Ban;
import com.vizako.anonphobease.repository.BanRepository;
import com.vizako.anonphobease.v1.dto.BanDTO;
import com.vizako.anonphobease.v1.dto.ChatDTO;
import com.vizako.anonphobease.v1.dto.MessageDTO;
import com.vizako.anonphobease.v1.dto.UserDTO;
import com.vizako.anonphobease.v1.mapper.BanMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
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
class BanServiceTest {

    @Mock
    private BanRepository banRepository;

    @Mock
    private UserService userService;

    @Mock
    private MessageService messageService;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private BanService banService;

    private ObjectId banId;
    private ObjectId userId;
    private ObjectId moderatorId;
    private ObjectId messageId;
    private ObjectId chatId;

    @BeforeEach
    void initIds() {
        banId = new ObjectId();
        userId = new ObjectId();
        moderatorId = new ObjectId();
        messageId = new ObjectId();
        chatId = new ObjectId();
    }


    @Test
    void findAll_returnsFullDtosWithRelatedData() {
        Ban ban = new Ban();
        ban.setId(banId);
        ban.setUserId(userId);
        ban.setModeratorId(moderatorId);
        ban.setMessageId(messageId);
        ban.setChatId(chatId);
        ban.setBanReason("Inappropriate content");
        ban.setBannedAt(new Date());

        when(banRepository.findAll()).thenReturn(List.of(ban));

        // user (banned)
        UserDTO userDto = UserDTO.builder()
                .id(userId.toHexString())
                .username("bannedUser")
                .build();
        when(userService.findById(userId.toString())).thenReturn(Optional.of(userDto));

        // moderator
        UserDTO moderatorDto = UserDTO.builder()
                .id(moderatorId.toHexString())
                .username("moderatorUser")
                .build();
        when(userService.findById(moderatorId.toString())).thenReturn(Optional.of(moderatorDto));

        // message
        MessageDTO messageDto = new MessageDTO();
        messageDto.setMessageId(messageId.toHexString());
        messageDto.setContent("Offensive message");
        when(messageService.findById(messageId.toString())).thenReturn(Optional.of(messageDto));

        // chat
        ChatDTO chatDto = new ChatDTO();
        chatDto.setId(chatId.toHexString());
        chatDto.setChatName("(EN) Social phobia");
        when(chatService.findById(chatId)).thenReturn(Optional.of(chatDto));

        List<BanDTO> result = banService.findAll();

        assertEquals(1, result.size());
        BanDTO dto = result.get(0);

        assertEquals(banId.toHexString(), dto.getId());

        // user
        assertEquals(userId.toHexString(), dto.getUserId());
        assertEquals("bannedUser", dto.getUsername());

        // moderator
        assertEquals(moderatorId.toHexString(), dto.getModeratorId());
        assertEquals("moderatorUser", dto.getModeratorName());

        // message
        assertEquals(messageId.toHexString(), dto.getMessageId());
        assertEquals("Offensive message", dto.getMessageContent());

        // chat
        assertEquals(chatId.toHexString(), dto.getChatId());
        assertEquals("(EN) Social phobia", dto.getChatName());

        assertEquals("Inappropriate content", dto.getBanReason());
    }


    @Test
    void toFullDTO_handlesNullRelatedIds() {
        Ban ban = new Ban();
        ban.setId(banId);
        ban.setBanReason("Reason");


        BanDTO dto = banService.toFullDTO(ban);

        assertEquals(banId.toHexString(), dto.getId());
        assertEquals("Reason", dto.getBanReason());
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertNull(dto.getModeratorId());
        assertNull(dto.getModeratorName());
        assertNull(dto.getMessageId());
        assertNull(dto.getMessageContent());
        assertNull(dto.getChatId());
        assertNull(dto.getChatName());

        verifyNoInteractions(userService, messageService, chatService);
    }


    @Test
    void findById_returnsDtoWhenFound() {
        Ban ban = new Ban();
        ban.setId(banId);
        ban.setBanReason("Reason");
        ban.setBannedAt(new Date());

        when(banRepository.findById(banId)).thenReturn(Optional.of(ban));

        Optional<BanDTO> result = banService.findById(banId.toHexString());

        assertTrue(result.isPresent());
        BanDTO dto = result.get();
        assertEquals(banId.toHexString(), dto.getId());
        assertEquals("Reason", dto.getBanReason());
    }

    @Test
    void findById_returnsEmptyWhenInvalidIdOrNotFound() {
        // invalid id
        Optional<BanDTO> result1 = banService.findById("not-an-objectid");
        assertTrue(result1.isEmpty());
        verifyNoInteractions(banRepository);

        // valid id
        when(banRepository.findById(banId)).thenReturn(Optional.empty());
        Optional<BanDTO> result2 = banService.findById(banId.toHexString());
        assertTrue(result2.isEmpty());
    }


    @Test
    void save_persistsEntityAndReturnsDto() {
        BanDTO dto = new BanDTO();
        dto.setUserId(userId.toHexString());
        dto.setChatId(chatId.toHexString());
        dto.setBanReason("Reason");

        Ban savedEntity = new Ban();
        savedEntity.setId(banId);
        savedEntity.setUserId(userId);
        savedEntity.setChatId(chatId);
        savedEntity.setBanReason("Reason");

        when(banRepository.save(any(Ban.class))).thenReturn(savedEntity);

        BanDTO result = banService.save(dto);

        assertEquals(banId.toHexString(), result.getId());
        assertEquals("Reason", result.getBanReason());

        ArgumentCaptor<Ban> captor = ArgumentCaptor.forClass(Ban.class);
        verify(banRepository).save(captor.capture());
        Ban toSave = captor.getValue();
        assertEquals(userId, toSave.getUserId());
        assertEquals(chatId, toSave.getChatId());
        assertEquals("Reason", toSave.getBanReason());
    }


    @Test
    void update_whenBanExists_updatesAndReturnsDto() {
        Ban existing = new Ban();
        existing.setId(banId);
        existing.setBanReason("Old reason");

        BanDTO dto = new BanDTO();
        dto.setId(banId.toHexString());
        dto.setBanReason("New reason");

        Ban savedAfterUpdate = new Ban();
        savedAfterUpdate.setId(banId);
        savedAfterUpdate.setBanReason("New reason");

        when(banRepository.findById(banId)).thenReturn(Optional.of(existing));
        when(banRepository.save(any(Ban.class))).thenReturn(savedAfterUpdate);

        Optional<BanDTO> result = banService.update(banId.toHexString(), dto);

        assertTrue(result.isPresent());
        BanDTO updated = result.get();
        assertEquals(banId.toHexString(), updated.getId());
        assertEquals("New reason", updated.getBanReason());
    }

    @Test
    void update_whenDtoIsNull_returnsEmptyAndDoesNothing() {
        Optional<BanDTO> result = banService.update(banId.toHexString(), null);

        assertTrue(result.isEmpty());
        verify(banRepository, never()).findById(any());
        verify(banRepository, never()).save(any());
    }

    @Test
    void update_whenBanNotFound_returnsEmpty() {
        BanDTO dto = new BanDTO();
        dto.setId(banId.toHexString());
        dto.setBanReason("Updated");

        when(banRepository.findById(banId)).thenReturn(Optional.empty());

        Optional<BanDTO> result = banService.update(banId.toHexString(), dto);

        assertTrue(result.isEmpty());
        verify(banRepository, never()).save(any());
    }


    @Test
    void deleteById_withValidId_callsRepositoryDelete() {
        banService.deleteById(banId.toHexString());

        ArgumentCaptor<ObjectId> captor = ArgumentCaptor.forClass(ObjectId.class);
        verify(banRepository).deleteById(captor.capture());
        assertEquals(banId.toHexString(), captor.getValue().toHexString());
    }

    @Test
    void deleteById_withInvalidId_doesNothing() {
        banService.deleteById("not-an-objectid");
        verifyNoInteractions(banRepository);
    }
}
