package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.repository.RoleRepository;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private UserLogService userLogService;

    @Autowired
    public void setUserLogService(@Lazy UserLogService userLogService) {
        this.userLogService = userLogService;
    }


    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoleDTO> findById(String id) {
        return roleRepository.findById(new ObjectId(id))
                .map(RoleMapper::toDTO);
    }

    public RoleDTO save(RoleDTO dto, String createdByUserId) {
        Role entity = RoleMapper.toEntity(dto);
        Role saved = roleRepository.save(entity);
        userLogService.save(createdByUserId, LogType.ROLE_SAVE, saved.getId().toHexString(), RelatedEntityType.ROLE);
        return RoleMapper.toDTO(saved);
    }

    public Optional<RoleDTO> update(String id, RoleDTO dto, String updatedByUserId) {
        if (dto == null) return Optional.empty();
        return roleRepository.findById(new ObjectId(id)).map(existingRole -> {
            RoleMapper.updateEntityFromDto(dto, existingRole);
            Role saved = roleRepository.save(existingRole);
            userLogService.save(updatedByUserId, LogType.ROLE_UPDATE, saved.getId().toHexString(), RelatedEntityType.ROLE);
            return RoleMapper.toDTO(saved);
        });
    }

    public Optional<RoleDTO> findByName(String name) {
        return roleRepository.findByName(name)
                .map(RoleMapper::toDTO);
    }

    public void deleteById(String id, String deletedByUserId) {
        userLogService.save(deletedByUserId, LogType.ROLE_DELETE, id, RelatedEntityType.ROLE);
        roleRepository.deleteById(new ObjectId(id));
    }
}
