package com.vizako.anonphobease.v1.mapper;

import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.model.User;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.dto.UserDTO;
import org.bson.types.ObjectId;

public class UserMapper {

    public static UserDTO toDTO(User entity, Role role) {
        if (entity == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId() != null ? entity.getId().toString() : null);
        dto.setUsername(entity.getUsername());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());

        if (role != null) {
            dto.setRole(new RoleDTO(role.getId().toString(), role.getName()));
        }

        return dto;
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User entity = new User();

        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }

        if (dto.getRole() != null && dto.getRole().getId() != null && ObjectId.isValid(dto.getRole().getId())) {
            entity.setRoleId(new ObjectId(dto.getRole().getId()));
        }

        entity.setUsername(dto.getUsername());
        entity.setIsActive(dto.getIsActive());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }

    public static void updateEntityFromDto(UserDTO dto, User entity) {
        if (dto == null || entity == null) return;

        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.setId(new ObjectId(dto.getId()));
        }
        if (dto.getRole() != null && dto.getRole().getId() != null && ObjectId.isValid(dto.getRole().getId())) {
            entity.setRoleId(new ObjectId(dto.getRole().getId()));
        }
        if (dto.getUsername() != null) {
            entity.setUsername(dto.getUsername());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getCreatedAt() != null) {
            entity.setCreatedAt(dto.getCreatedAt());
        }
    }

}
