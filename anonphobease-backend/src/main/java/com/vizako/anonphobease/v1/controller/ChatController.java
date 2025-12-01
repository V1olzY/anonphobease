package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.v1.dto.ChatDTO;
import com.vizako.anonphobease.v1.dto.ChatsResponseDTO;
import com.vizako.anonphobease.v1.mapper.ChatMapper;
import com.vizako.anonphobease.model.Chat;
import com.vizako.anonphobease.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ChatsResponseDTO> getAllChatsWithFilters() {
        ChatsResponseDTO response = chatService.getAllChatsWithFilters();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ChatDTO getById(@PathVariable String id) {
        return chatService.findById(new ObjectId(id))
                .orElse(null);

    }

    @PostMapping
    public ChatDTO create(HttpServletRequest request, @Valid @RequestBody ChatDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return chatService.save(dto, adminId);
    }

    @PutMapping("/{id}")
    public ChatDTO update(HttpServletRequest request, @PathVariable String id, @Valid @RequestBody ChatDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return chatService.update(id, dto,adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable String id) {
        String adminId = (String) request.getAttribute("adminId");
        chatService.deleteById(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin")
    public List<ChatDTO> getAllForAdmin() {
        return chatService.findAll();
    }

}
