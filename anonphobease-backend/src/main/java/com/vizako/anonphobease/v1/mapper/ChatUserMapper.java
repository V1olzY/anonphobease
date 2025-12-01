package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.ChatUser;
import com.vizako.anonphobease.v1.dto.ChatUserDTO;
import org.bson.types.ObjectId;

public class ChatUserMapper {

    public static ChatUserDTO toDTO(ChatUser entity) {
        if (entity == null) return null;
        ChatUserDTO dto = new ChatUserDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setChatId(entity.getChatId() != null ? entity.getChatId().toString() : null);
        dto.setUserId(entity.getUserId() != null ? entity.getUserId().toString() : null);
        dto.setJoinedAt(entity.getJoinedAt());
        return dto;
    }

    public static ChatUser toEntity(ChatUserDTO dto) {
        if (dto == null) return null;
        ChatUser entity = new ChatUser();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getChatId() != null && ObjectId.isValid(dto.getChatId())) {
            entity.setChatId(new ObjectId(dto.getChatId()));
        }
        if (dto.getUserId() != null && ObjectId.isValid(dto.getUserId())) {
            entity.setUserId(new ObjectId(dto.getUserId()));
        }
        entity.setJoinedAt(dto.getJoinedAt());
        return entity;
    }

    public static void updateEntityFromDto(ChatUserDTO dto, ChatUser entity) {
        if (dto == null || entity == null) return;

        if (dto.getChatId() != null && ObjectId.isValid(dto.getChatId())) {
            entity.setChatId(new ObjectId(dto.getChatId()));
        }
        if (dto.getUserId() != null && ObjectId.isValid(dto.getUserId())) {
            entity.setUserId(new ObjectId(dto.getUserId()));
        }
        if (dto.getJoinedAt() != null) {
            entity.setJoinedAt(dto.getJoinedAt());
        }
    }
}
