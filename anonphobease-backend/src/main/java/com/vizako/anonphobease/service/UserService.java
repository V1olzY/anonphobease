package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.User;
import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.repository.RoleRepository;
import com.vizako.anonphobease.repository.UserRepository;
import com.vizako.anonphobease.v1.dto.UserDTO;
import com.vizako.anonphobease.v1.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    Role role = findRoleById(user.getRoleId().toString());
                    return UserMapper.toDTO(user, role);
                })
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findById(String id) {
        return userRepository.findById(new ObjectId(id))
                .map(user -> {
                    Role role = findRoleById(user.getRoleId().toString());
                    return UserMapper.toDTO(user, role);
                });
    }

    public String getUserNameById(String id) {
        return userRepository.findById(new ObjectId(id))
                .map(User::getUsername)
                .orElse("Unknown User");
    }

    public UserDTO save(UserDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }

        User user = UserMapper.toEntity(dto);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Date());
        }

        User saved = userRepository.save(user);
        Role role = findRoleById(saved.getRoleId().toString());
        return UserMapper.toDTO(saved, role);
    }


    public Optional<UserDTO> update(String id, UserDTO dto) {
        if (dto == null) {
            return Optional.empty();
        }

        return userRepository.findById(new ObjectId(id)).map(existingUser -> {
            UserMapper.updateEntityFromDto(dto, existingUser);

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            User saved = userRepository.save(existingUser);
            Role role = findRoleById(saved.getRoleId().toString());
            return UserMapper.toDTO(saved, role);
        });
    }


    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public Role findRoleById(String roleId) {
        return roleRepository.findById(new ObjectId(roleId))
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
    }

    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    Role role = findRoleById(user.getRoleId().toString());
                    return UserMapper.toDTO(user, role);
                });
    }

    public void updateIsActive(String id, boolean isActive) {
        userRepository.findById(new ObjectId(id))
                .ifPresent(user -> {
                    user.setIsActive(isActive);
                    userRepository.save(user);
                });
    }


}
