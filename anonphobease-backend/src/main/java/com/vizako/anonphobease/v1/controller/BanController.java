package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.v1.dto.BanDTO;
import com.vizako.anonphobease.v1.mapper.BanMapper;
import com.vizako.anonphobease.model.Ban;
import com.vizako.anonphobease.service.BanService;
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
@RequestMapping("/v1/bans")
public class BanController {

    @Autowired
    private BanService banService;

    @GetMapping
    public List<BanDTO> getAll() {
        return banService.findAll();
    }

    @GetMapping("/{id}")
    public BanDTO getById(@PathVariable String id) {
        return banService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ban not found"));
    }

    @PostMapping
    public BanDTO create(@Valid @RequestBody BanDTO dto) {
        return banService.save(dto);
    }

    @PutMapping("/{id}")
    public BanDTO update(@PathVariable String id, @Valid @RequestBody BanDTO dto) {
        return banService.update(id, dto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ban not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        banService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
