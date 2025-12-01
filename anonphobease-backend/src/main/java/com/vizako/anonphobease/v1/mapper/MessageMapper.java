package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Message;
import com.vizako.anonphobease.v1.dto.MessageDTO;
import org.bson.types.ObjectId;

public class MessageMapper {

    public static MessageDTO toDTO(Message entity) {
        if (entity == null) return null;
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(toString(entity.getId()));
        dto.setUserId(toString(entity.getUserId()));
        dto.setChatId(toString(entity.getChatId()));
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public static Message toEntity(MessageDTO dto) {
        if (dto == null) return null;
        Message entity = new Message();
        entity.setId(toObjectId(dto.getMessageId()));
        entity.setUserId(toObjectId(dto.getUserId()));
        entity.setChatId(toObjectId(dto.getChatId()));
        entity.setContent(dto.getContent());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }

    public static void updateEntityFromDto(MessageDTO dto, Message entity) {
        if (dto == null || entity == null) return;

        if (dto.getMessageId() != null && ObjectId.isValid(dto.getMessageId())) {
            entity.setId(new ObjectId(dto.getMessageId()));
        }
        if (dto.getUserId() != null && ObjectId.isValid(dto.getUserId())) {
            entity.setUserId(new ObjectId(dto.getUserId()));
        }
        if (dto.getChatId() != null && ObjectId.isValid(dto.getChatId())) {
            entity.setChatId(new ObjectId(dto.getChatId()));
        }
        if (dto.getContent() != null) {
            entity.setContent(dto.getContent());
        }
        if (dto.getCreatedAt() != null) {
            entity.setCreatedAt(dto.getCreatedAt());
        }
    }

    private static String toString(ObjectId objectId) {
        return objectId != null ? objectId.toString() : null;
    }

    private static ObjectId toObjectId(String id) {
        return id != null && ObjectId.isValid(id) ? new ObjectId(id) : null;
    }
}