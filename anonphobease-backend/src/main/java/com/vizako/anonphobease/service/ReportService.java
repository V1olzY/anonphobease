package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.ActionType;
import com.vizako.anonphobease.model.Report;
import com.vizako.anonphobease.repository.ReportRepository;
import com.vizako.anonphobease.v1.dto.BanDTO;
import com.vizako.anonphobease.v1.dto.ReportActionRequestDTO;
import com.vizako.anonphobease.v1.dto.ReportDTO;
import com.vizako.anonphobease.v1.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final BanService banService;

    public List<ReportDTO> findAll() {
        return reportRepository.findAll().stream()
                .map(this::toFullDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReportDTO> findById(String id) {
        return reportRepository.findById(new ObjectId(id))
                .map(ReportMapper::toDTO);
    }

    public ReportDTO save(ReportDTO dto) {
        dto.setActionTaken(ActionType.PENDING);
        Report entity = ReportMapper.toEntity(dto);
        Report saved = reportRepository.save(entity);
        return ReportMapper.toDTO(saved);
    }


    public Optional<ReportDTO> update(String id, ReportDTO dto) {
        if (dto == null) return Optional.empty();
        return reportRepository.findById(new ObjectId(id)).map(existingReport -> {
            ReportMapper.updateEntityFromDto(dto, existingReport);
            Report saved = reportRepository.save(existingReport);
            return ReportMapper.toDTO(saved);
        });
    }

    public void deleteById(String id) {
        reportRepository.deleteById(new ObjectId(id));
    }

    public ReportDTO toFullDTO(Report report) {
        ReportDTO dto = ReportMapper.toDTO(report);

        if (report.getReporterUserId() != null) {
            dto.setReporterUserId(report.getReporterUserId().toHexString());
            userService.findById(report.getReporterUserId().toString()).ifPresent(
                    user -> dto.setReporterUsername(user.getUsername())
            );
        }

        if (report.getReportedUserId() != null) {
            dto.setReportedUserId(report.getReportedUserId().toHexString());
            userService.findById(report.getReportedUserId().toString()).ifPresent(
                    user -> dto.setReportedUsername(user.getUsername())
            );
        }
        if (report.getChatId() != null) {
            dto.setChatId(report.getChatId().toHexString());
            chatService.findById(report.getChatId()).ifPresent(
                    chat -> dto.setChatName(chat.getChatName())
            );
        }

        if (report.getMessageId() != null) {
            dto.setMessageId(report.getMessageId().toHexString());
            messageService.findById(report.getMessageId().toString()).ifPresent(
                    message -> dto.setMessageContent(message.getContent())
            );
        }
        if (report.getModeratorId() != null) {
            dto.setModeratorId(report.getModeratorId().toHexString());
            userService.findById(report.getModeratorId().toString()).ifPresent(
                    user -> dto.setModeratorName(user.getUsername())
            );
        }


        return dto;
    }

    public Optional<ReportDTO> markNoViolation(String id, ReportActionRequestDTO reportActionRequestDTO) {
        Optional<Report> reportOpt = reportRepository.findById(new ObjectId(id));
        if (reportOpt.isPresent()) {
            Report report = reportOpt.get();
            report.setModeratorId(new ObjectId(reportActionRequestDTO.getModeratorId()));
            report.setActionReason(reportActionRequestDTO.getActionReason());
            report.setActionTaken(ActionType.NOT_BANNED);
            report.setIsResolved(true);
            report.setResolvedAt(new Date());
            reportRepository.save(report);
            return Optional.of(toFullDTO(report));
        }
        return Optional.empty();
    }

    public Optional<ReportDTO> banReport(String id, ReportActionRequestDTO reportActionRequestDTO) {
        Optional<Report> reportOpt = reportRepository.findById(new ObjectId(id));
        if (reportOpt.isEmpty()) {
            return Optional.empty();
        }

        Report report = reportOpt.get();

        Date now = new Date();

        report.setModeratorId(new ObjectId(reportActionRequestDTO.getModeratorId()));
        report.setActionReason(reportActionRequestDTO.getActionReason());
        report.setActionTaken(ActionType.BANNED);
        report.setIsResolved(true);
        report.setResolvedAt(now);
        reportRepository.save(report);


        BanDTO banDTO = new BanDTO();
        if (report.getReportedUserId() != null) {
            banDTO.setUserId(report.getReportedUserId().toHexString());
        }
        if (report.getChatId() != null) {
            banDTO.setChatId(report.getChatId().toHexString());
        }
        if (report.getMessageId() != null) {
            banDTO.setMessageId(report.getMessageId().toHexString());
        }
        if (report.getModeratorId() != null) {
            banDTO.setModeratorId(report.getModeratorId().toHexString());
        }

        String banReason = report.getActionReason() != null && !report.getActionReason().isBlank()
                ? report.getActionReason()
                : report.getReason();
        banDTO.setBanReason(banReason);
        banDTO.setBannedAt(now);

        banService.save(banDTO);

        return Optional.of(toFullDTO(report));
    }



}
