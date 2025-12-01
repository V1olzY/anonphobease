package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Chat;
import com.vizako.anonphobease.v1.dto.ChatDTO;
import org.bson.types.ObjectId;

public class ChatMapper {

    public static ChatDTO toDTO(Chat entity) {
        if (entity == null) return null;
        ChatDTO dto = new ChatDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setLanguageId(entity.getLanguageId() != null ? entity.getLanguageId().toString() : null);
        dto.setPhobiaId(entity.getPhobiaId() != null ? entity.getPhobiaId().toString() : null);
        dto.setChatName(entity.getName() != null ? entity.getName() : null);
        return dto;

    }

    public static Chat toEntity(ChatDTO dto) {
        if (dto == null) return null;
        Chat entity = new Chat();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getLanguageId() != null && ObjectId.isValid(dto.getLanguageId())) {
            entity.setLanguageId(new ObjectId(dto.getLanguageId()));
        }
        if (dto.getPhobiaId() != null && ObjectId.isValid(dto.getPhobiaId())) {
            entity.setPhobiaId(new ObjectId(dto.getPhobiaId()));
        }
        if (dto.getChatName() != null && ObjectId.isValid(dto.getChatName())) {
            entity.setName(dto.getChatName());
        }
        return entity;
    }

    public static void updateEntityFromDto(ChatDTO dto, Chat entity) {
        if (dto == null || entity == null) return;

        if (dto.getLanguageId() != null && ObjectId.isValid(dto.getLanguageId())) {
            entity.setLanguageId(new ObjectId(dto.getLanguageId()));
        }
        if (dto.getPhobiaId() != null && ObjectId.isValid(dto.getPhobiaId())) {
            entity.setPhobiaId(new ObjectId(dto.getPhobiaId()));
        }
        if (dto.getChatName() != null && !dto.getChatName().equals(entity.getName())) {
            entity.setName(dto.getChatName());
        }
    }
}
