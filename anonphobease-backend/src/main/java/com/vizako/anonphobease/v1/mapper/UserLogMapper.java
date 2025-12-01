package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.UserLog;
import com.vizako.anonphobease.v1.dto.UserLogDTO;
import org.bson.types.ObjectId;

public class UserLogMapper {

    public static UserLogDTO toDTO(UserLog entity, String userName, String relatedEntityName, String relatedEntityExtra) {
        if (entity == null) return null;
        UserLogDTO dto = new UserLogDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setUserId(entity.getUserId() != null ? entity.getUserId().toString() : null);
        dto.setLogType(entity.getLogType() != null ? entity.getLogType() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setDetails(entity.getDetails());
        dto.setUserName(userName);
        dto.setRelatedEntityId(entity.getRelatedEntityId() != null ? entity.getRelatedEntityId().toString() : null);
        dto.setRelatedEntityType(entity.getRelatedEntityType());
        dto.setRelatedEntityName(relatedEntityName);
        dto.setRelatedEntityExtra(relatedEntityExtra);
        return dto;
    }

    public static UserLog toEntity(UserLogDTO dto) {
        if (dto == null) return null;
        UserLog entity = new UserLog();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getUserId() != null && ObjectId.isValid(dto.getUserId())) {
            entity.setUserId(new ObjectId(dto.getUserId()));
        }
        if (dto.getLogType() != null) {
            entity.setLogType(LogType.valueOf(dto.getLogType().name()));
        }
        entity.setRelatedEntityType(dto.getRelatedEntityType());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setDetails(dto.getDetails());
        if (dto.getRelatedEntityId() != null && ObjectId.isValid(dto.getRelatedEntityId())) {
            entity.setRelatedEntityId(new ObjectId(dto.getRelatedEntityId()));
        }
        return entity;
    }

    public static void updateEntityFromDto(UserLogDTO dto, UserLog entity) {
        if (dto == null || entity == null) return;

        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getUserId() != null && ObjectId.isValid(dto.getUserId())) {
            entity.setUserId(new ObjectId(dto.getUserId()));
        }
        if (dto.getLogType() != null) {
            entity.setLogType(LogType.valueOf(dto.getLogType().name()));
        }
        if (dto.getCreatedAt() != null) {
            entity.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getDetails() != null) {
            entity.setDetails(dto.getDetails());
        }
        if (dto.getRelatedEntityId() != null) {
            entity.setRelatedEntityType(dto.getRelatedEntityType());
        }
        if (dto.getRelatedEntityId() != null && ObjectId.isValid(dto.getRelatedEntityId())) {
            entity.setRelatedEntityId(new ObjectId(dto.getRelatedEntityId()));
        }
    }
}
