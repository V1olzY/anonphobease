package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.model.User;
import com.vizako.anonphobease.repository.RoleRepository;
import com.vizako.anonphobease.repository.UserRepository;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.dto.UserDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;


    @Test
    void findAll_returnsDtosWithRoles() {
        ObjectId roleId1 = new ObjectId();
        ObjectId roleId2 = new ObjectId();

        User user1 = new User();
        user1.setId(new ObjectId());
        user1.setUsername("alice");
        user1.setRoleId(roleId1);

        User user2 = new User();
        user2.setId(new ObjectId());
        user2.setUsername("bob");
        user2.setRoleId(roleId2);

        Role role1 = new Role();
        role1.setId(roleId1);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(roleId2);
        role2.setName("ROLE_ADMIN");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(roleRepository.findById(roleId1)).thenReturn(Optional.of(role1));
        when(roleRepository.findById(roleId2)).thenReturn(Optional.of(role2));

        List<UserDTO> result = userService.findAll();

        assertEquals(2, result.size());

        UserDTO dto1 = result.get(0);
        UserDTO dto2 = result.get(1);

        assertEquals("alice", dto1.getUsername());
        assertEquals("ROLE_USER", dto1.getRole().getName());

        assertEquals("bob", dto2.getUsername());
        assertEquals("ROLE_ADMIN", dto2.getRole().getName());
    }


    @Test
    void findById_returnsDto() {
        ObjectId userId = new ObjectId();
        ObjectId roleId = new ObjectId();

        User user = new User();
        user.setId(userId);
        user.setUsername("alice");
        user.setRoleId(roleId);

        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Optional<UserDTO> result = userService.findById(userId.toHexString());

        assertTrue(result.isPresent());
        UserDTO dto = result.get();

        assertEquals(userId.toHexString(), dto.getId());
        assertEquals("alice", dto.getUsername());
        assertEquals("ROLE_USER", dto.getRole().getName());
    }

    @Test
    void findById_returnsEmptyIfNotFound() {
        ObjectId userId = new ObjectId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findById(userId.toHexString());

        assertTrue(result.isEmpty());
    }


    @Test
    void getUserNameById_returnsUsername() {
        ObjectId userId = new ObjectId();

        User user = new User();
        user.setId(userId);
        user.setUsername("alice");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String name = userService.getUserNameById(userId.toHexString());

        assertEquals("alice", name);
    }

    @Test
    void getUserNameById_returnsUnknownIfMissing() {
        ObjectId userId = new ObjectId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String name = userService.getUserNameById(userId.toHexString());

        assertEquals("Unknown User", name);
    }


    @Test
    void save_savesUserAndReturnsDto() {
        ObjectId roleId = new ObjectId();

        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        UserDTO dto = UserDTO.builder()
                .id(null)
                .username("alice")
                .role(RoleDTO.builder().id(roleId.toHexString()).name("ROLE_USER").build())
                .build();

        User savedUser = new User();
        savedUser.setId(new ObjectId());
        savedUser.setUsername("alice");
        savedUser.setRoleId(roleId);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        UserDTO result = userService.save(dto);

        assertEquals("alice", result.getUsername());
        assertEquals("ROLE_USER", result.getRole().getName());
    }


    @Test
    void update_updatesAndReturnsDto() {
        ObjectId userId = new ObjectId();
        ObjectId roleId = new ObjectId();

        User existing = new User();
        existing.setId(userId);
        existing.setUsername("oldName");
        existing.setRoleId(roleId);

        UserDTO dto = UserDTO.builder()
                .id(userId.toHexString())
                .username("newName")
                .role(RoleDTO.builder().id(roleId.toHexString()).name("ROLE_USER").build())
                .build();

        User saved = new User();
        saved.setId(userId);
        saved.setUsername("newName");
        saved.setRoleId(roleId);

        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(saved);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Optional<UserDTO> result = userService.update(userId.toHexString(), dto);

        assertTrue(result.isPresent());
        UserDTO updated = result.get();

        assertEquals("newName", updated.getUsername());
        assertEquals("ROLE_USER", updated.getRole().getName());
    }

    @Test
    void update_returnsEmptyIfUserMissing() {
        ObjectId id = new ObjectId();

        UserDTO dto = UserDTO.builder()
                .id(id.toHexString())
                .username("newName")
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.update(id.toHexString(), dto);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_returnsEmptyIfDtoIsNull() {
        ObjectId id = new ObjectId();

        Optional<UserDTO> result = userService.update(id.toHexString(), null);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).findById(any());
    }


    @Test
    void deleteById_callsDelete() {
        ObjectId id = new ObjectId();

        userService.deleteById(id);

        verify(userRepository).deleteById(id);
    }


    @Test
    void findRoleById_returnsRole() {
        ObjectId roleId = new ObjectId();

        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Role result = userService.findRoleById(roleId.toHexString());

        assertEquals("ROLE_USER", result.getName());
    }

    @Test
    void findRoleById_throwsIfMissing() {
        ObjectId roleId = new ObjectId();

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.findRoleById(roleId.toHexString())
        );
    }


    @Test
    void findByUsername_returnsDto() {
        ObjectId userId = new ObjectId();
        ObjectId roleId = new ObjectId();

        User user = new User();
        user.setId(userId);
        user.setUsername("alice");
        user.setRoleId(roleId);

        Role role = new Role();
        role.setId(roleId);
        role.setName("ROLE_USER");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Optional<UserDTO> result = userService.findByUsername("alice");

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
        assertEquals("ROLE_USER", result.get().getRole().getName());
    }

    @Test
    void findByUsername_returnsEmptyIfMissing() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findByUsername("unknown");

        assertTrue(result.isEmpty());
    }
}
