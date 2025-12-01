package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.repository.RoleRepository;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserLogService userLogService;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setup() {
        roleService.setUserLogService(userLogService);
    }


    @Test
    void findAll_returnsMappedDtos() {
        Role r1 = new Role();
        r1.setId(new ObjectId());
        r1.setName("USER");

        Role r2 = new Role();
        r2.setId(new ObjectId());
        r2.setName("ADMIN");

        when(roleRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RoleDTO> result = roleService.findAll();

        assertEquals(2, result.size());
        assertEquals("USER", result.get(0).getName());
        assertEquals("ADMIN", result.get(1).getName());
    }


    @Test
    void findById_returnsDtoWhenExists() {
        ObjectId id = new ObjectId();

        Role role = new Role();
        role.setId(id);
        role.setName("USER");

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        Optional<RoleDTO> result = roleService.findById(id.toHexString());

        assertTrue(result.isPresent());
        RoleDTO dto = result.get();
        assertEquals(id.toHexString(), dto.getId());
        assertEquals("USER", dto.getName());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        ObjectId id = new ObjectId();

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        Optional<RoleDTO> result = roleService.findById(id.toHexString());

        assertTrue(result.isEmpty());
    }


    @Test
    void save_savesRoleAndCreatesLog() {
        String userId = new ObjectId().toHexString();

        RoleDTO dto = RoleDTO.builder()
                .id(null)
                .name("USER")
                .build();

        ObjectId savedId = new ObjectId();
        Role savedEntity = new Role();
        savedEntity.setId(savedId);
        savedEntity.setName("USER");

        when(roleRepository.save(any(Role.class))).thenReturn(savedEntity);

        RoleDTO result = roleService.save(dto, userId);

        assertEquals(savedId.toHexString(), result.getId());
        assertEquals("USER", result.getName());

        verify(userLogService).save(
                eq(userId),
                eq(LogType.ROLE_SAVE),
                eq(savedId.toHexString()),
                eq(RelatedEntityType.ROLE)
        );
    }


    @Test
    void update_whenRoleExists_updatesAndCreatesLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        Role existing = new Role();
        existing.setId(id);
        existing.setName("OLD_ROLE");

        RoleDTO dto = RoleDTO.builder()
                .id(idStr)
                .name("NEW_ROLE")
                .build();

        Role savedAfterUpdate = new Role();
        savedAfterUpdate.setId(id);
        savedAfterUpdate.setName("NEW_ROLE");

        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.save(any(Role.class))).thenReturn(savedAfterUpdate);

        Optional<RoleDTO> result = roleService.update(idStr, dto, userId);

        assertTrue(result.isPresent());
        RoleDTO updated = result.get();
        assertEquals(idStr, updated.getId());
        assertEquals("NEW_ROLE", updated.getName());

        verify(userLogService).save(
                eq(userId),
                eq(LogType.ROLE_UPDATE),
                eq(idStr),
                eq(RelatedEntityType.ROLE)
        );
    }

    @Test
    void update_whenDtoIsNull_returnsEmptyAndDoesNothing() {
        String idStr = new ObjectId().toHexString();
        String userId = new ObjectId().toHexString();

        Optional<RoleDTO> result = roleService.update(idStr, null, userId);

        assertTrue(result.isEmpty());
        verify(roleRepository, never()).findById(any());
        verify(roleRepository, never()).save(any());
        verify(userLogService, never()).save(anyString(), any(), anyString(), any());
    }

    @Test
    void update_whenRoleNotFound_returnsEmptyAndNoLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        RoleDTO dto = RoleDTO.builder()
                .id(idStr)
                .name("NEW_ROLE")
                .build();

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        Optional<RoleDTO> result = roleService.update(idStr, dto, userId);

        assertTrue(result.isEmpty());
        verify(roleRepository, never()).save(any());
        verify(userLogService, never()).save(anyString(), any(), anyString(), any());
    }


    @Test
    void findByName_returnsDtoWhenExists() {
        Role role = new Role();
        role.setId(new ObjectId());
        role.setName("ADMIN");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        Optional<RoleDTO> result = roleService.findByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    @Test
    void findByName_returnsEmptyWhenNotFound() {
        when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        Optional<RoleDTO> result = roleService.findByName("UNKNOWN");

        assertTrue(result.isEmpty());
    }


    @Test
    void deleteById_deletesRoleAndCreatesLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        roleService.deleteById(idStr, userId);

        verify(userLogService).save(
                eq(userId),
                eq(LogType.ROLE_DELETE),
                eq(idStr),
                eq(RelatedEntityType.ROLE)
        );
        verify(roleRepository).deleteById(eq(id));
    }
}
