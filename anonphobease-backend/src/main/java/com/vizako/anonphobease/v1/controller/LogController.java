 package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.service.UserLogService;
import com.vizako.anonphobease.v1.dto.UserLogDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/logs")
public class LogController {

    private final UserLogService userLogService;

    public LogController(UserLogService userLogService) {
        this.userLogService = userLogService;
    }

    @GetMapping
    public List<UserLogDTO> getAllLogs() {
        return userLogService.findAll();
    }

    @GetMapping("/{userId}")
    public List<UserLogDTO> getLogsByUserId(@PathVariable String userId) {
        return userLogService.findByUserId(userId);
    }
}
