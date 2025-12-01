package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.v1.dto.PhobiaDTO;
import com.vizako.anonphobease.v1.mapper.PhobiaMapper;
import com.vizako.anonphobease.model.Phobia;
import com.vizako.anonphobease.service.PhobiaService;
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
@RequestMapping("/v1/phobias")
public class PhobiaController {

    @Autowired
    private PhobiaService phobiaService;

    @GetMapping
    public List<PhobiaDTO> getAll() {
        return phobiaService.findAll();
    }

    @GetMapping("/{id}")
    public PhobiaDTO getById(@PathVariable String id) {
        return phobiaService.findById(id)
                .orElse(null);

    }

    @PostMapping
    public PhobiaDTO create(HttpServletRequest request, @Valid @RequestBody PhobiaDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return phobiaService.save(dto, adminId);
    }

    @PutMapping("/{id}")
    public PhobiaDTO update(HttpServletRequest request, @PathVariable String id, @Valid @RequestBody PhobiaDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return phobiaService.update(id, dto, adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Phobia not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable String id) {
        String adminId = (String) request.getAttribute("adminId");
        phobiaService.deleteById(id, adminId);
        return ResponseEntity.noContent().build();
    }
}

