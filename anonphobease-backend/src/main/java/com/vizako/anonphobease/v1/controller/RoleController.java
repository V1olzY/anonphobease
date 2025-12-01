package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.v1.dto.RoleDTO;
import com.vizako.anonphobease.v1.mapper.RoleMapper;
import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.service.RoleService;
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
@RequestMapping("/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<RoleDTO> getAll() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public RoleDTO getById(@PathVariable String id) {
        return roleService.findById(id).orElse(null);
    }

    @PostMapping
    public RoleDTO create(HttpServletRequest request, @Valid @RequestBody RoleDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return roleService.save(dto, adminId);
    }

    @PutMapping("/{id}")
    public RoleDTO update(HttpServletRequest request, @PathVariable String id, @Valid @RequestBody RoleDTO dto) {
        String adminId = (String) request.getAttribute("adminId");
        return roleService.update(id, dto, adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable String id) {
        String adminId = (String) request.getAttribute("adminId");
        roleService.deleteById(id, adminId);
        return ResponseEntity.noContent().build();
    }
}
