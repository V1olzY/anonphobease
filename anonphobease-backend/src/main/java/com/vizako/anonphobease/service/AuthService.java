package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.LoginRequest;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.dto.UserDTO;
import com.vizako.anonphobease.v1.dto.UserLogDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserLogService userLogService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> login(@Valid LoginRequest request) {
        Optional<UserDTO> optionalUser = userService.findByUsername(request.getUsername());

        if (optionalUser.isPresent()) {
            UserDTO existingUser = optionalUser.get();
            RoleDTO role = existingUser.getRole();

            switch (role.getName()) {
                case "ADMIN", "MODERATOR" -> {
                    if (request.getPassword() == null || request.getPassword().isBlank()) {
                        throw new IllegalArgumentException("Parool on kohustuslik");
                    }

                    try {
                        Authentication auth = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                        );
                        UserDetails userDetails = (UserDetails) auth.getPrincipal();
                        String token = jwtUtil.generateToken(userDetails.getUsername(), role.getName(), existingUser.getId());
                        userService.updateIsActive(existingUser.getId(), true);
                        userLogService.save(existingUser.getId(), LogType.USER_LOGIN, null, RelatedEntityType.USER);
                        return Map.of(
                                "token", token,
                                "userId", existingUser.getId(),
                                "username", existingUser.getUsername(),
                                "role", role.getName(),
                                "roleId", existingUser.getRole().getId(),
                                "isActive", existingUser.getIsActive(),
                                "createdAt", existingUser.getCreatedAt()
                        );
                    } catch (BadCredentialsException e) {
                        throw e;
                    }
                }
                case "USER" -> {
                    if (Boolean.TRUE.equals(existingUser.getIsActive())) {
                        throw new BadCredentialsException("Nickname already in use");
                    }
                    userService.updateIsActive(existingUser.getId(), true);
                    String token = jwtUtil.generateToken(request.getUsername(), "USER", existingUser.getId());


                    userLogService.save(existingUser.getId(), LogType.USER_LOGIN, null, RelatedEntityType.USER);
                    return Map.of(
                            "token", token,
                            "userId", existingUser.getId(),
                            "username", existingUser.getUsername(),
                            "role", role.getName(),
                            "roleId", existingUser.getRole().getId(),
                            "isActive", existingUser.getIsActive(),
                            "createdAt", existingUser.getCreatedAt()
                    );
                }
                default -> throw new SecurityException("Tundmatu roll");
            }
        } else {
            RoleDTO defaultRole = roleService.findByName("USER").orElseThrow();
            UserDTO tempUser = UserDTO.builder()
                    .id(new ObjectId().toString())
                    .username(request.getUsername())
                    .role(defaultRole)
                    .isActive(true)
                    .createdAt(new Date())
                    .build();

            UserDTO newUser = userService.save(tempUser);
            String token = jwtUtil.generateToken(request.getUsername(), "USER", newUser.getId());
            userLogService.save(newUser.getId(), LogType.USER_LOGIN, null, RelatedEntityType.USER);
            return Map.of(
                    "token", token,
                    "userId", newUser.getId(),
                    "username", newUser.getUsername(),
                    "role", defaultRole.getName(),
                    "roleId", newUser.getRole().getId(),
                    "isActive", newUser.getIsActive(),
                    "createdAt", newUser.getCreatedAt()
            );
        }
    }

    public boolean checkNicknameRequiresPassword(String nickname) {
        Optional<UserDTO> userOpt = userService.findByUsername(nickname);
        if (userOpt.isPresent()) {
            String roleId = userOpt.get().getRole().getId();
            Optional<RoleDTO> roleOpt = roleService.findById(roleId);
            if (roleOpt.isPresent()) {
                String roleName = roleOpt.get().getName();
                return roleName.equals("ADMIN") || roleName.equals("MODERATOR");
            }
        }
        return false;
    }

    public String extractToken(String authHeader, String tokenParam, Map<String, String> body) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else if (tokenParam != null) {
            return tokenParam;
        } else if (body != null && body.get("token") != null) {
            return body.get("token");
        }
        return null;
    }

    public boolean deactivateUserByToken(String token) {
        if (token == null) {
            return false;
        }
        String userId;
        try {
            userId = jwtUtil.extractUserId(token);
        } catch (Exception e) {
            return false;
        }
        try {
            Optional<UserDTO> userOpt = userService.findById(userId);
            userOpt.ifPresent(user -> {
                userService.updateIsActive(userId, false);
                userLogService.save(userId, LogType.USER_LOGOUT, null, RelatedEntityType.USER);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}