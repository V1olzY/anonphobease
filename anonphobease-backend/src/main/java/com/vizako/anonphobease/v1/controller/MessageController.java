package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.v1.dto.MessageDTO;
import com.vizako.anonphobease.v1.mapper.MessageMapper;
import com.vizako.anonphobease.model.Message;
import com.vizako.anonphobease.service.MessageService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public List<MessageDTO> getAll() {
        return messageService.findAll();
    }

    @GetMapping("/{id}")
    public MessageDTO getById(@PathVariable String id) {
        return messageService.findById(id)
                .orElse(null);

    }

    @PostMapping
    public MessageDTO create(@Valid @RequestBody MessageDTO dto) {
        return messageService.save(dto);
    }

    @PutMapping("/{id}")
    public MessageDTO update(@PathVariable String id, @Valid @RequestBody MessageDTO dto) {
        return messageService.update(id, dto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        messageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
