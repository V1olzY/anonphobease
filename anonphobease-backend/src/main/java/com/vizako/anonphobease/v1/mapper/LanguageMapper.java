package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Language;
import com.vizako.anonphobease.v1.dto.LanguageDTO;
import org.bson.types.ObjectId;

public class LanguageMapper {

    public static LanguageDTO toDTO(Language entity) {
        if (entity == null) return null;
        LanguageDTO dto = new LanguageDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        return dto;
    }

    public static Language toEntity(LanguageDTO dto) {
        if (dto == null) return null;
        Language entity = new Language();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        return entity;
    }

    public static void updateEntityFromDto(LanguageDTO dto, Language entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
    }
}
