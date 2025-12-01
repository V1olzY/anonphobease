package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.LoginRequest;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserLogService userLogService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_existingInactiveUserWithUserRole_activatesAndReturnsToken() {
        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword(null);

        RoleDTO role = RoleDTO.builder()
                .id("role-user-id")
                .name("USER")
                .build();

        UserDTO existingUser = UserDTO.builder()
                .id("user-id-123")
                .username("user1")
                .role(role)
                .isActive(false)
                .build();

        when(userService.findByUsername("user1"))
                .thenReturn(Optional.of(existingUser));

        when(jwtUtil.generateToken("user1", "USER", "user-id-123"))
                .thenReturn("jwt-token");

        // when
        Map<String, Object> result = authService.login(request);

        // then
        assertEquals("jwt-token", result.get("token"));
        assertEquals("user-id-123", result.get("userId"));
        assertEquals("user1", result.get("username"));
        assertEquals("USER", result.get("role"));
        assertEquals("role-user-id", result.get("roleId"));

        verify(userService).save(existingUser);

        verify(userLogService).save(
                "user-id-123",
                LogType.USER_LOGIN,
                null,
                RelatedEntityType.USER
        );
    }

    @Test
    void login_existingActiveUserWithUserRole_throwsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");

        RoleDTO role = RoleDTO.builder()
                .id("role-user-id")
                .name("USER")
                .build();

        UserDTO existingUser = UserDTO.builder()
                .id("user-id-123")
                .username("user1")
                .role(role)
                .isActive(true)
                .build();

        when(userService.findByUsername("user1"))
                .thenReturn(Optional.of(existingUser));


        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
        verify(userService, never()).save(any());
    }

    @Test
    void checkNicknameRequiresPassword_admin_returnsTrue() {
        // given
        String nickname = "admin";

        RoleDTO role = RoleDTO.builder()
                .id("role-admin-id")
                .name("ADMIN")
                .build();

        UserDTO user = UserDTO.builder()
                .id("user-id-1")
                .username(nickname)
                .role(role)
                .build();

        when(userService.findByUsername(nickname))
                .thenReturn(Optional.of(user));

        when(roleService.findById("role-admin-id"))
                .thenReturn(Optional.of(role));

        // when
        boolean needsPassword = authService.checkNicknameRequiresPassword(nickname);

        // then
        assertTrue(needsPassword);
    }

    @Test
    void checkNicknameRequiresPassword_user_returnsFalse() {
        // given
        String nickname = "user1";

        RoleDTO role = RoleDTO.builder()
                .id("role-user-id")
                .name("USER")
                .build();

        UserDTO user = UserDTO.builder()
                .id("user-id-2")
                .username(nickname)
                .role(role)
                .build();

        when(userService.findByUsername(nickname))
                .thenReturn(Optional.of(user));

        when(roleService.findById("role-user-id"))
                .thenReturn(Optional.of(role));

        // when
        boolean needsPassword = authService.checkNicknameRequiresPassword(nickname);

        // then
        assertFalse(needsPassword);
    }

    @Test
    void checkNicknameRequiresPassword_userNotFound_returnsFalse() {
        // given
        String nickname = "ghost";

        when(userService.findByUsername(nickname))
                .thenReturn(Optional.empty());

        // when
        boolean needsPassword = authService.checkNicknameRequiresPassword(nickname);

        // then
        assertFalse(needsPassword);
    }


    @Test
    void deactivateUserByToken_validToken_userDeactivatedAndLogged() {
        // given
        String token = "valid-token";
        String userId = "user-id-1";

        RoleDTO role = RoleDTO.builder()
                .id("role-user-id")
                .name("USER")
                .build();

        UserDTO user = UserDTO.builder()
                .id(userId)
                .username("user1")
                .role(role)
                .isActive(true)
                .build();

        when(jwtUtil.extractUserId(token))
                .thenReturn(userId);

        when(userService.findById(userId))
                .thenReturn(Optional.of(user));

        // when
        boolean result = authService.deactivateUserByToken(token);

        // then
        assertTrue(result);
        assertFalse(user.getIsActive());

        verify(userService).save(user);
        verify(userLogService).save(
                userId,
                LogType.USER_LOGOUT,
                null,
                RelatedEntityType.USER
        );
    }

    @Test
    void deactivateUserByToken_nullToken_returnsFalse() {
        // when
        boolean result = authService.deactivateUserByToken(null);

        // then
        assertFalse(result);
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(userService);
        verifyNoInteractions(userLogService);
    }

    @Test
    void deactivateUserByToken_invalidToken_returnsFalse() {
        // given
        String token = "bad-token";

        when(jwtUtil.extractUserId(token))
                .thenThrow(new RuntimeException("invalid token"));

        // when
        boolean result = authService.deactivateUserByToken(token);

        // then
        assertFalse(result);
        verify(userService, never()).findById(anyString());
        verify(userService, never()).save(any());
        verify(userLogService, never()).save(any(), any(), any(), any());
    }

    @Test
    void extractToken_fromAuthorizationHeader() {
        String header = "Bearer abc123";

        String token = authService.extractToken(header, null, null);

        assertEquals("abc123", token);
    }

    @Test
    void extractToken_fromQueryParam() {
        String token = authService.extractToken(null, "qwerty", null);

        assertEquals("qwerty", token);
    }

    @Test
    void extractToken_fromBody() {
        Map<String, String> body = Map.of("token", "zzz");

        String token = authService.extractToken(null, null, body);

        assertEquals("zzz", token);
    }

    @Test
    void extractToken_noneProvided_returnsNull() {
        String token = authService.extractToken(null, null, null);

        assertNull(token);
    }

}
