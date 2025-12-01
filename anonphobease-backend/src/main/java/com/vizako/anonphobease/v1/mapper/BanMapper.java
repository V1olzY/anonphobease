package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Ban;
import com.vizako.anonphobease.v1.dto.BanDTO;
import org.bson.types.ObjectId;

public class BanMapper {

    public static BanDTO toDTO(Ban entity) {
        if (entity == null) return null;
        BanDTO dto = new BanDTO();

        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setUserId(entity.getUserId() != null ? entity.getUserId().toString() : null);
        dto.setChatId(entity.getChatId() != null ? entity.getChatId().toString() : null);
        dto.setMessageId(entity.getMessageId() != null ? entity.getMessageId().toString() : null);
        dto.setModeratorId(entity.getModeratorId() != null ? entity.getModeratorId().toString() : null);
        dto.setBanReason(entity.getBanReason());
        dto.setBannedAt(entity.getBannedAt());


        return dto;
    }

    public static Ban toEntity(BanDTO dto) {
        if (dto == null) return null;
        Ban entity = new Ban();

        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }

        if (dto.getUserId() != null && ObjectId.isValid(dto.getUserId())) {
            entity.setUserId(new ObjectId(dto.getUserId()));
        }

        if (dto.getChatId() != null && ObjectId.isValid(dto.getChatId())) {
            entity.setChatId(new ObjectId(dto.getChatId()));
        }
        if (dto.getMessageId() != null && ObjectId.isValid(dto.getMessageId())) {
            entity.setMessageId(new ObjectId(dto.getMessageId()));
        }
        if (dto.getModeratorId() != null && ObjectId.isValid(dto.getModeratorId())) {
            entity.setModeratorId(new ObjectId(dto.getModeratorId()));
        }

        entity.setBanReason(dto.getBanReason());
        entity.setBannedAt(dto.getBannedAt());

        return entity;
    }

    public static void updateEntityFromDto(BanDTO dto, Ban entity) {
        if (dto == null || entity == null) return;

        if (dto.getUserId() != null && org.bson.types.ObjectId.isValid(dto.getUserId())) {
            entity.setUserId(new org.bson.types.ObjectId(dto.getUserId()));
        }
        if (dto.getChatId() != null && org.bson.types.ObjectId.isValid(dto.getChatId())) {
            entity.setChatId(new org.bson.types.ObjectId(dto.getChatId()));
        }
        if (dto.getMessageId() != null && org.bson.types.ObjectId.isValid(dto.getMessageId())) {
            entity.setMessageId(new org.bson.types.ObjectId(dto.getMessageId()));
        }
        if (dto.getModeratorId() != null && org.bson.types.ObjectId.isValid(dto.getModeratorId())) {
            entity.setModeratorId(new org.bson.types.ObjectId(dto.getModeratorId()));
        }
        if (dto.getBanReason() != null) {
            entity.setBanReason(dto.getBanReason());
        }
        if (dto.getBannedAt() != null) {
            entity.setBannedAt(dto.getBannedAt());
        }
    }

}
