package com.vizako.anonphobease.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportActionRequestDTO {
    @NotBlank(message = "Moderator id is required")
    private String moderatorId;

    @NotBlank(message = "Action reason is required")
    @Size(max = 500, message = "Action reason must be at most 500 characters")
    private String actionReason;
}