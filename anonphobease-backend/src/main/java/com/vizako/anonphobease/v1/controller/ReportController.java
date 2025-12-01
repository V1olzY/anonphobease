package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.v1.dto.ReportActionRequestDTO;
import com.vizako.anonphobease.v1.dto.ReportDTO;
import com.vizako.anonphobease.v1.mapper.ReportMapper;
import com.vizako.anonphobease.model.Report;
import com.vizako.anonphobease.service.ReportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/reports")
public class ReportController {

    private ReportService reportService;

    @GetMapping
    public List<ReportDTO> getAll() {
        return reportService.findAll();
    }

    @GetMapping("/{id}")
    public ReportDTO getById(@PathVariable String id) {
        return reportService.findById(id)
                .orElse(null);

    }

    @PostMapping
    public ReportDTO create(@Valid @RequestBody ReportDTO dto) {
        dto.setCreatedAt(new Date());
        return reportService.save(dto);

    }


    @PutMapping("/{id}")
    public ReportDTO update(@PathVariable String id, @Valid @RequestBody ReportDTO dto) {
        return reportService.update(id, dto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        reportService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/no-violation")
    public ReportDTO markNoViolation(
            @PathVariable String id,
            @Valid @RequestBody ReportActionRequestDTO actionRequest) {
        return reportService.markNoViolation(id, actionRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    @PostMapping("/{id}/ban")
    public ReportDTO banReport(
            @PathVariable String id,
            @Valid @RequestBody ReportActionRequestDTO actionRequest) {
        return reportService.banReport(id, actionRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }
}
