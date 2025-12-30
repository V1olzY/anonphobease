package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.service.*;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.model.User;
import com.vizako.anonphobease.repository.RoleRepository;
import com.vizako.anonphobease.repository.UserRepository;
import com.vizako.anonphobease.v1.dto.LoginRequest;
import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CaptchaService captchaService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
       if (!captchaService.verifyToken(request.getCaptcha())) {
            return ResponseEntity.badRequest().body("Captcha verification failed");
        }
        try {
            Map<String, Object> response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean requiresPassword = authService.checkNicknameRequiresPassword(nickname);
        return ResponseEntity.ok(Map.of("requiresPassword", requiresPassword));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String tokenParam) {
        String token = authService.extractToken(authHeader, tokenParam, null);
        boolean success = authService.deactivateUserByToken(token);
        return success ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/user-left")
    public ResponseEntity<Void> userLeft(@RequestBody Map<String, String> body) {
        String token = authService.extractToken(null, null, body);
        boolean success = authService.deactivateUserByToken(token);
        return success ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

