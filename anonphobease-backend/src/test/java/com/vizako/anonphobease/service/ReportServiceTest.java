package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.ActionType;
import com.vizako.anonphobease.model.Report;
import com.vizako.anonphobease.repository.ReportRepository;
import com.vizako.anonphobease.v1.dto.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @Mock
    private ChatService chatService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ReportService reportService;

    private ObjectId reportId;
    private ObjectId chatId;
    private ObjectId reporterId;
    private ObjectId reportedId;
    private ObjectId messageId;
    private ObjectId moderatorId;

    @BeforeEach
    void initIds() {
        reportId = new ObjectId();
        chatId = new ObjectId();
        reporterId = new ObjectId();
        reportedId = new ObjectId();
        messageId = new ObjectId();
        moderatorId = new ObjectId();
    }


    @Test
    void findAll_returnsFullDtosWithRelatedData() {
        Report report = new Report();
        report.setId(reportId);
        report.setChatId(chatId);
        report.setReporterUserId(reporterId);
        report.setReportedUserId(reportedId);
        report.setMessageId(messageId);
        report.setModeratorId(moderatorId);
        report.setReason("Bad behavior");
        report.setActionTaken(ActionType.PENDING);

        when(reportRepository.findAll()).thenReturn(List.of(report));

        // reporter
        UserDTO reporterDto = UserDTO.builder()
                .id(reporterId.toHexString())
                .username("reporterUser")
                .build();

        // reported
        UserDTO reportedDto = UserDTO.builder()
                .id(reportedId.toHexString())
                .username("reportedUser")
                .build();

        // moderator
        UserDTO moderatorDto = UserDTO.builder()
                .id(moderatorId.toHexString())
                .username("moderatorUser")
                .build();

        when(userService.findById(reporterId.toString())).thenReturn(Optional.of(reporterDto));
        when(userService.findById(reportedId.toString())).thenReturn(Optional.of(reportedDto));
        when(userService.findById(moderatorId.toString())).thenReturn(Optional.of(moderatorDto));

        ChatDTO chatDto = new ChatDTO();
        chatDto.setId(chatId.toHexString());
        chatDto.setChatName("(EN) Social phobia");
        when(chatService.findById(chatId)).thenReturn(Optional.of(chatDto));

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageId(messageId.toHexString());
        messageDTO.setContent("Offensive message");
        when(messageService.findById(messageId.toString())).thenReturn(Optional.of(messageDTO));

        List<ReportDTO> result = reportService.findAll();

        assertEquals(1, result.size());
        ReportDTO dto = result.get(0);

        assertEquals(reportId.toHexString(), dto.getId());
        assertEquals(chatId.toHexString(), dto.getChatId());
        assertEquals("(EN) Social phobia", dto.getChatName());

        assertEquals(reporterId.toHexString(), dto.getReporterUserId());
        assertEquals("reporterUser", dto.getReporterUsername());

        assertEquals(reportedId.toHexString(), dto.getReportedUserId());
        assertEquals("reportedUser", dto.getReportedUsername());

        assertEquals(messageId.toHexString(), dto.getMessageId());
        assertEquals("Offensive message", dto.getMessageContent());

        assertEquals(moderatorId.toHexString(), dto.getModeratorId());
        assertEquals("moderatorUser", dto.getModeratorName());
    }

    // ---------- findById ----------

    @Test
    void findById_returnsDtoWhenFound() {
        Report report = new Report();
        report.setId(reportId);
        report.setReason("Reason");

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        Optional<ReportDTO> result = reportService.findById(reportId.toHexString());

        assertTrue(result.isPresent());
        assertEquals(reportId.toHexString(), result.get().getId());
        assertEquals("Reason", result.get().getReason());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        Optional<ReportDTO> result = reportService.findById(reportId.toHexString());

        assertTrue(result.isEmpty());
    }


    @Test
    void save_setsActionPendingAndSaves() {
        ReportDTO dto = new ReportDTO();
        dto.setChatId(chatId.toHexString());
        dto.setReporterUserId(reporterId.toHexString());
        dto.setReportedUserId(reportedId.toHexString());
        dto.setMessageId(messageId.toHexString());
        dto.setReason("Some reason");

        Report savedEntity = new Report();
        savedEntity.setId(reportId);
        savedEntity.setChatId(chatId);
        savedEntity.setReporterUserId(reporterId);
        savedEntity.setReportedUserId(reportedId);
        savedEntity.setMessageId(messageId);
        savedEntity.setReason("Some reason");
        savedEntity.setActionTaken(ActionType.PENDING);

        when(reportRepository.save(any(Report.class))).thenReturn(savedEntity);

        ReportDTO result = reportService.save(dto);

        assertEquals(reportId.toHexString(), result.getId());
        assertEquals(ActionType.PENDING, result.getActionTaken());

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());
        assertEquals(ActionType.PENDING, captor.getValue().getActionTaken());
    }


    @Test
    void update_whenReportExists_updatesAndReturnsDto() {
        Report existing = new Report();
        existing.setId(reportId);
        existing.setReason("Old reason");
        existing.setActionTaken(ActionType.PENDING);

        ReportDTO dto = new ReportDTO();
        dto.setId(reportId.toHexString());
        dto.setReason("Updated reason");
        dto.setActionTaken(ActionType.NOT_BANNED);

        Report savedAfterUpdate = new Report();
        savedAfterUpdate.setId(reportId);
        savedAfterUpdate.setReason("Updated reason");
        savedAfterUpdate.setActionTaken(ActionType.NOT_BANNED);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(existing));
        when(reportRepository.save(any(Report.class))).thenReturn(savedAfterUpdate);

        Optional<ReportDTO> result = reportService.update(reportId.toHexString(), dto);

        assertTrue(result.isPresent());
        ReportDTO updated = result.get();
        assertEquals(reportId.toHexString(), updated.getId());
        assertEquals("Updated reason", updated.getReason());
        assertEquals(ActionType.NOT_BANNED, updated.getActionTaken());
    }

    @Test
    void update_whenDtoIsNull_returnsEmptyAndDoesNothing() {
        Optional<ReportDTO> result = reportService.update(reportId.toHexString(), null);

        assertTrue(result.isEmpty());
        verify(reportRepository, never()).findById(any());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void update_whenReportNotFound_returnsEmpty() {
        ReportDTO dto = new ReportDTO();
        dto.setId(reportId.toHexString());
        dto.setReason("Updated");

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        Optional<ReportDTO> result = reportService.update(reportId.toHexString(), dto);

        assertTrue(result.isEmpty());
        verify(reportRepository, never()).save(any());
    }


    @Test
    void deleteById_callsRepositoryDelete() {
        reportService.deleteById(reportId.toHexString());

        ArgumentCaptor<ObjectId> captor = ArgumentCaptor.forClass(ObjectId.class);
        verify(reportRepository).deleteById(captor.capture());

        assertEquals(reportId.toHexString(), captor.getValue().toHexString());
    }


    @Test
    void toFullDTO_handlesNullRelatedIds() {
        Report report = new Report();
        report.setId(reportId);
        report.setReason("Reason");

        ReportDTO dto = reportService.toFullDTO(report);

        assertEquals(reportId.toHexString(), dto.getId());
        assertEquals("Reason", dto.getReason());
        assertNull(dto.getReporterUserId());
        assertNull(dto.getReportedUserId());
        assertNull(dto.getChatId());
        assertNull(dto.getMessageId());
        assertNull(dto.getModeratorId());

        verifyNoInteractions(userService, chatService, messageService);
    }


    @Test
    void markNoViolation_whenReportExists_updatesFieldsAndReturnsFullDto() {
        Report report = new Report();
        report.setId(reportId);
        report.setChatId(chatId);
        report.setReporterUserId(reporterId);
        report.setReportedUserId(reportedId);
        report.setMessageId(messageId);
        report.setReason("Reason");

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        ReportActionRequestDTO actionRequest = new ReportActionRequestDTO();
        actionRequest.setModeratorId(moderatorId.toHexString());
        actionRequest.setActionReason("No violation");

        UserDTO reporterDto = UserDTO.builder()
                .id(reporterId.toHexString())
                .username("reporterUser")
                .build();
        UserDTO reportedDto = UserDTO.builder()
                .id(reportedId.toHexString())
                .username("reportedUser")
                .build();
        UserDTO moderatorDto = UserDTO.builder()
                .id(moderatorId.toHexString())
                .username("moderatorUser")
                .build();

        when(userService.findById(reporterId.toString())).thenReturn(Optional.of(reporterDto));
        when(userService.findById(reportedId.toString())).thenReturn(Optional.of(reportedDto));
        when(userService.findById(moderatorId.toString())).thenReturn(Optional.of(moderatorDto));

        ChatDTO chatDto = new ChatDTO();
        chatDto.setId(chatId.toHexString());
        chatDto.setChatName("Chat name");
        when(chatService.findById(chatId)).thenReturn(Optional.of(chatDto));

        MessageDTO msgDto = new MessageDTO();
        msgDto.setMessageId(messageId.toHexString());
        msgDto.setContent("Msg");
        when(messageService.findById(messageId.toString())).thenReturn(Optional.of(msgDto));

        Optional<ReportDTO> result = reportService.markNoViolation(reportId.toHexString(), actionRequest);

        assertTrue(result.isPresent());
        ReportDTO dto = result.get();

        assertEquals(ActionType.NOT_BANNED, dto.getActionTaken());
        assertTrue(dto.getIsResolved());
        assertEquals("No violation", dto.getActionReason());
        assertEquals(moderatorId.toHexString(), dto.getModeratorId());
        assertEquals("moderatorUser", dto.getModeratorName());
        assertNotNull(dto.getResolvedAt());

        assertEquals("Chat name", dto.getChatName());

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());
        Report saved = captor.getValue();
        assertEquals(ActionType.NOT_BANNED, saved.getActionTaken());
        assertTrue(saved.getIsResolved());
        assertEquals("No violation", saved.getActionReason());
        assertEquals(moderatorId, saved.getModeratorId());
        assertNotNull(saved.getResolvedAt());
    }


    @Test
    void markNoViolation_whenReportNotFound_returnsEmpty() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        ReportActionRequestDTO actionRequest = new ReportActionRequestDTO();
        actionRequest.setModeratorId(moderatorId.toHexString());
        actionRequest.setActionReason("No violation");

        Optional<ReportDTO> result = reportService.markNoViolation(reportId.toHexString(), actionRequest);

        assertTrue(result.isEmpty());
        verify(reportRepository, never()).save(any());
    }


    @Test
    void banReport_whenReportExists_updatesFieldsAndReturnsFullDto() {
        // given
        Report report = new Report();
        report.setId(reportId);
        report.setChatId(chatId);
        report.setReporterUserId(reporterId);
        report.setReportedUserId(reportedId);
        report.setMessageId(messageId);
        report.setReason("Reason");

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        ReportActionRequestDTO actionRequest = new ReportActionRequestDTO();
        actionRequest.setModeratorId(moderatorId.toHexString());
        actionRequest.setActionReason("User banned");

        // reporter / reported / moderator
        UserDTO reporterDto = UserDTO.builder()
                .id(reporterId.toHexString())
                .username("reporterUser")
                .build();
        UserDTO reportedDto = UserDTO.builder()
                .id(reportedId.toHexString())
                .username("reportedUser")
                .build();
        UserDTO moderatorDto = UserDTO.builder()
                .id(moderatorId.toHexString())
                .username("moderatorUser")
                .build();

        when(userService.findById(reporterId.toString())).thenReturn(Optional.of(reporterDto));
        when(userService.findById(reportedId.toString())).thenReturn(Optional.of(reportedDto));
        when(userService.findById(moderatorId.toString())).thenReturn(Optional.of(moderatorDto));

        ChatDTO chatDto = new ChatDTO();
        chatDto.setId(chatId.toHexString());
        chatDto.setChatName("Chat name");
        when(chatService.findById(chatId)).thenReturn(Optional.of(chatDto));

        MessageDTO msgDto = new MessageDTO();
        msgDto.setMessageId(messageId.toHexString());
        msgDto.setContent("Msg");
        when(messageService.findById(messageId.toString())).thenReturn(Optional.of(msgDto));

        // when
        Optional<ReportDTO> result = reportService.banReport(reportId.toHexString(), actionRequest);

        // then
        assertTrue(result.isPresent());
        ReportDTO dto = result.get();

        assertEquals(ActionType.BANNED, dto.getActionTaken());
        assertTrue(dto.getIsResolved());
        assertEquals("User banned", dto.getActionReason());
        assertEquals(moderatorId.toHexString(), dto.getModeratorId());
        assertEquals("moderatorUser", dto.getModeratorName());
        assertNotNull(dto.getResolvedAt());


        assertEquals("Chat name", dto.getChatName());
        assertEquals(chatId.toHexString(), dto.getChatId());

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());
        Report saved = captor.getValue();
        assertEquals(ActionType.BANNED, saved.getActionTaken());
        assertTrue(saved.getIsResolved());
        assertEquals("User banned", saved.getActionReason());
        assertEquals(moderatorId, saved.getModeratorId());
        assertNotNull(saved.getResolvedAt());
    }


    @Test
    void banReport_whenReportNotFound_returnsEmpty() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        ReportActionRequestDTO actionRequest = new ReportActionRequestDTO();
        actionRequest.setModeratorId(moderatorId.toHexString());
        actionRequest.setActionReason("User banned");

        Optional<ReportDTO> result = reportService.banReport(reportId.toHexString(), actionRequest);

        assertTrue(result.isEmpty());
        verify(reportRepository, never()).save(any());
    }
}
