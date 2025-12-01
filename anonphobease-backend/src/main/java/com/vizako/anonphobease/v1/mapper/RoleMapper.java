package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import org.bson.types.ObjectId;

public class RoleMapper {

    public static RoleDTO toDTO(Role entity) {
        if (entity == null) return null;
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setName(entity.getName());
        return dto;
    }

    public static Role toEntity(RoleDTO dto) {
        if (dto == null) return null;
        Role entity = new Role();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        entity.setName(dto.getName());
        return entity;
    }

    public static void updateEntityFromDto(RoleDTO dto, Role entity) {
        if (dto == null || entity == null) return;

        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}
