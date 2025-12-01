package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.v1.dto.LanguageDTO;
import com.vizako.anonphobease.v1.mapper.LanguageMapper;
import com.vizako.anonphobease.model.Language;
import com.vizako.anonphobease.service.LanguageService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/v1/languages")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    @GetMapping
    public List<LanguageDTO> getAll() {
        return languageService.findAll();
    }

    @GetMapping("/{id}")
    public LanguageDTO getById(@PathVariable String id) {
        return languageService.findById(id).orElse(null);

    }

    @PostMapping
    public LanguageDTO create( HttpServletRequest request,@Valid @RequestBody LanguageDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return languageService.save(dto, adminId);
    }

    @PutMapping("/{id}")
    public LanguageDTO update(HttpServletRequest request, @PathVariable String id, @Valid @RequestBody LanguageDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return languageService.update(id, dto, adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable String id) {
        String adminId = (String) request.getAttribute("adminId");
        languageService.deleteById(id, adminId);
        return ResponseEntity.noContent().build();
    }
}
