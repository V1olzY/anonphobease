package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Report;
import com.vizako.anonphobease.v1.dto.ReportDTO;
import org.bson.types.ObjectId;

public class ReportMapper {

    public static ReportDTO toDTO(Report entity) {
        if (entity == null) return null;
        ReportDTO dto = new ReportDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setChatId(entity.getChatId() != null ? entity.getChatId().toString() : null);
        dto.setReportedUserId(entity.getReportedUserId() != null ? entity.getReportedUserId().toString() : null);
        dto.setReporterUserId(entity.getReporterUserId() != null ? entity.getReporterUserId().toString() : null);
        dto.setModeratorId(entity.getModeratorId() != null ? entity.getModeratorId().toString() : null);
        dto.setMessageId(entity.getMessageId() != null ? entity.getMessageId().toString() : null);
        dto.setResolvedAt(entity.getResolvedAt() != null ? entity.getResolvedAt() : null);
        dto.setReason(entity.getReason());
        dto.setActionTaken(entity.getActionTaken());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setActionReason(entity.getActionReason());
        dto.setIsResolved(entity.getIsResolved());
        return dto;
    }

    public static Report toEntity(ReportDTO dto) {
        if (dto == null) return null;
        Report entity = new Report();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getChatId() != null && ObjectId.isValid(dto.getChatId())) {
            entity.setChatId(new ObjectId(dto.getChatId()));
        }
        if (dto.getReportedUserId() != null && ObjectId.isValid(dto.getReportedUserId())) {
            entity.setReportedUserId(new ObjectId(dto.getReportedUserId()));
        }
        if (dto.getReporterUserId() != null && ObjectId.isValid(dto.getReporterUserId())) {
            entity.setReporterUserId(new ObjectId(dto.getReporterUserId()));
        }
        if (dto.getModeratorId() != null && ObjectId.isValid(dto.getModeratorId())) {
            entity.setModeratorId(new ObjectId(dto.getModeratorId()));
        }
        if (dto.getMessageId() != null && ObjectId.isValid(dto.getMessageId())) {
            entity.setMessageId(new ObjectId(dto.getMessageId()));
        }

        if (dto.getResolvedAt() != null) {
            entity.setResolvedAt(dto.getResolvedAt());
        }

        entity.setReason(dto.getReason());
        entity.setActionTaken(dto.getActionTaken());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setActionReason(dto.getActionReason());
        return entity;
    }

    public static void updateEntityFromDto(ReportDTO dto, Report entity) {
        if (dto == null || entity == null) return;

        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getChatId() != null && ObjectId.isValid(dto.getChatId())) {
            entity.setChatId(new ObjectId(dto.getChatId()));
        }
        if (dto.getReportedUserId() != null && ObjectId.isValid(dto.getReportedUserId())) {
            entity.setReportedUserId(new ObjectId(dto.getReportedUserId()));
        }
        if (dto.getReporterUserId() != null && ObjectId.isValid(dto.getReporterUserId())) {
            entity.setReporterUserId(new ObjectId(dto.getReporterUserId()));
        }
        if (dto.getModeratorId() != null && ObjectId.isValid(dto.getModeratorId())) {
            entity.setModeratorId(new ObjectId(dto.getModeratorId()));
        }
        if (dto.getMessageId() != null && ObjectId.isValid(dto.getMessageId())) {
            entity.setMessageId(new ObjectId(dto.getMessageId()));
        }
        if (dto.getReason() != null) {
            entity.setReason(dto.getReason());
        }
        if (dto.getActionTaken() != null) {
            entity.setActionTaken(dto.getActionTaken());
        }
        if (dto.getCreatedAt() != null) {
            entity.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getActionReason() != null) {
            entity.setActionReason(dto.getActionReason());
        }
        if (dto.getResolvedAt() != null) {
            entity.setResolvedAt(dto.getResolvedAt());
        }
    }


}
