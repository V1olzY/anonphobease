package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Phobia;
import com.vizako.anonphobease.v1.dto.PhobiaDTO;
import org.bson.types.ObjectId;

public class PhobiaMapper {

    public static PhobiaDTO toDTO(Phobia entity) {
        if (entity == null) return null;
        PhobiaDTO dto = new PhobiaDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public static Phobia toEntity(PhobiaDTO dto) {
        if (dto == null) return null;
        Phobia entity = new Phobia();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public static void updateEntityFromDto(PhobiaDTO dto, Phobia entity) {
        if (dto == null || entity == null) return;

        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}
