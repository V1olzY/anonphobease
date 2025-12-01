package com.vizako.anonphobease.service;

import com.vizako.anonphobease.helper.RelatedEntityHelperService;
import com.vizako.anonphobease.model.*;
import com.vizako.anonphobease.repository.UserLogRepository;
import com.vizako.anonphobease.v1.dto.UserLogDTO;
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
class UserLogServiceTest {

    @Mock
    private UserLogRepository userLogRepository;

    @Mock
    private UserService userService;

    @Mock
    private RelatedEntityHelperService relatedEntityHelperService;

    @InjectMocks
    private UserLogService userLogService;

    private ObjectId logId;
    private ObjectId userId;
    private ObjectId relatedEntityId;

    @BeforeEach
    void init() {
        logId = new ObjectId();
        userId = new ObjectId();
        relatedEntityId = new ObjectId();
    }


    @Test
    void findAll_returnsEnrichedDtos() {
        UserLog log = new UserLog();
        log.setId(logId);
        log.setUserId(userId);
        log.setLogType(LogType.CHAT_SAVE);
        log.setCreatedAt(new Date());
        log.setDetails("some details");
        log.setRelatedEntityId(relatedEntityId);
        log.setRelatedEntityType(RelatedEntityType.CHAT);

        when(userLogRepository.findAll()).thenReturn(List.of(log));
        when(userService.getUserNameById(userId.toString())).thenReturn("Alice");

        RelatedEntityInfo info = new RelatedEntityInfo("Chat 1", "extra info");
        when(relatedEntityHelperService.getRelatedEntityInfo(log)).thenReturn(info);

        List<UserLogDTO> result = userLogService.findAll();

        assertEquals(1, result.size());
        UserLogDTO dto = result.get(0);

        assertEquals(logId.toHexString(), dto.getId());
        assertEquals(userId.toHexString(), dto.getUserId());
        assertEquals(LogType.CHAT_SAVE, dto.getLogType());
        assertEquals(relatedEntityId.toHexString(), dto.getRelatedEntityId());
        assertEquals(RelatedEntityType.CHAT, dto.getRelatedEntityType());

        verify(userService).getUserNameById(userId.toString());
        verify(relatedEntityHelperService).getRelatedEntityInfo(log);
    }

    // -------- findById --------

    @Test
    void findById_returnsEnrichedDtoWhenFound() {
        UserLog log = new UserLog();
        log.setId(logId);
        log.setUserId(userId);
        log.setLogType(LogType.CHAT_SAVE);
        log.setCreatedAt(new Date());
        log.setDetails("some details");
        log.setRelatedEntityId(relatedEntityId);
        log.setRelatedEntityType(RelatedEntityType.CHAT);

        when(userLogRepository.findById(logId)).thenReturn(Optional.of(log));
        when(userService.getUserNameById(userId.toString())).thenReturn("Alice");

        RelatedEntityInfo info = new RelatedEntityInfo("Chat 1", "extra info");
        when(relatedEntityHelperService.getRelatedEntityInfo(log)).thenReturn(info);

        Optional<UserLogDTO> result = userLogService.findById(logId.toHexString());

        assertTrue(result.isPresent());
        UserLogDTO dto = result.get();

        assertEquals(logId.toHexString(), dto.getId());
        assertEquals(userId.toHexString(), dto.getUserId());
        assertEquals(LogType.CHAT_SAVE, dto.getLogType());
        assertEquals(relatedEntityId.toHexString(), dto.getRelatedEntityId());
        assertEquals(RelatedEntityType.CHAT, dto.getRelatedEntityType());

        verify(userService).getUserNameById(userId.toString());
        verify(relatedEntityHelperService).getRelatedEntityInfo(log);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        when(userLogRepository.findById(logId)).thenReturn(Optional.empty());

        Optional<UserLogDTO> result = userLogService.findById(logId.toHexString());

        assertTrue(result.isEmpty());
        verifyNoInteractions(userService, relatedEntityHelperService);
    }


    @Test
    void findByUserId_returnsEnrichedDtos() {
        UserLog log = new UserLog();
        log.setId(logId);
        log.setUserId(userId);
        log.setLogType(LogType.CHAT_SAVE);
        log.setCreatedAt(new Date());
        log.setDetails("some details");
        log.setRelatedEntityId(relatedEntityId);
        log.setRelatedEntityType(RelatedEntityType.CHAT);

        when(userLogRepository.findByUserId(userId)).thenReturn(List.of(log));
        when(userService.getUserNameById(userId.toString())).thenReturn("Alice");

        RelatedEntityInfo info = new RelatedEntityInfo("Chat 1", "extra info");
        when(relatedEntityHelperService.getRelatedEntityInfo(log)).thenReturn(info);

        List<UserLogDTO> result = userLogService.findByUserId(userId.toHexString());

        assertEquals(1, result.size());
        UserLogDTO dto = result.get(0);

        assertEquals(logId.toHexString(), dto.getId());
        assertEquals(userId.toHexString(), dto.getUserId());
        assertEquals(LogType.CHAT_SAVE, dto.getLogType());
        assertEquals(relatedEntityId.toHexString(), dto.getRelatedEntityId());
        assertEquals(RelatedEntityType.CHAT, dto.getRelatedEntityType());

        verify(userLogRepository).findByUserId(userId);
        verify(userService).getUserNameById(userId.toString());
        verify(relatedEntityHelperService).getRelatedEntityInfo(log);
    }


    @Test
    void save_createsUserLogWithTemplateAndSaves() {
        String userIdStr = userId.toHexString();
        String relatedIdStr = relatedEntityId.toHexString();
        LogType logType = LogType.CHAT_SAVE;
        RelatedEntityType relatedType = RelatedEntityType.CHAT;

        UserLog savedEntity = new UserLog();
        savedEntity.setId(logId);
        savedEntity.setUserId(userId);
        savedEntity.setLogType(logType);
        savedEntity.setCreatedAt(new Date());
        savedEntity.setDetails(logType.getTemplate());
        savedEntity.setRelatedEntityId(relatedEntityId);
        savedEntity.setRelatedEntityType(relatedType);

        when(userLogRepository.save(any(UserLog.class))).thenReturn(savedEntity);

        UserLogDTO result = userLogService.save(userIdStr, logType, relatedIdStr, relatedType);


        assertEquals(logId.toHexString(), result.getId());
        assertEquals(userIdStr, result.getUserId());
        assertEquals(logType, result.getLogType());
        assertEquals(relatedIdStr, result.getRelatedEntityId());
        assertEquals(relatedType, result.getRelatedEntityType());
        assertEquals(logType.getTemplate(), result.getDetails());
        assertNotNull(result.getCreatedAt());


        ArgumentCaptor<UserLog> captor = ArgumentCaptor.forClass(UserLog.class);
        verify(userLogRepository).save(captor.capture());
        UserLog saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(logType, saved.getLogType());
        assertEquals(relatedEntityId, saved.getRelatedEntityId());
        assertEquals(relatedType, saved.getRelatedEntityType());
        assertEquals(logType.getTemplate(), saved.getDetails());
        assertNotNull(saved.getCreatedAt());
    }


    @Test
    void deleteById_callsRepositoryDelete() {
        userLogService.deleteById(logId.toHexString());

        ArgumentCaptor<ObjectId> captor = ArgumentCaptor.forClass(ObjectId.class);
        verify(userLogRepository).deleteById(captor.capture());

        assertEquals(logId.toHexString(), captor.getValue().toHexString());
    }
}
