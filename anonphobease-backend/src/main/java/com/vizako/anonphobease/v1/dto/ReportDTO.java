package com.vizako.anonphobease.v1.dto;

import com.vizako.anonphobease.model.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String id;

    @NotBlank(message = "Chat id is required")
    private String chatId;

    private String chatName;

    @NotBlank(message = "Reported user id is required")
    private String reportedUserId;

    private String reportedUsername;

    @NotBlank(message = "Reporter user id is required")
    private String reporterUserId;

    private String reporterUsername;
    private String moderatorId;
    private String moderatorName;

    @NotBlank(message = "Message id is required")
    private String messageId;
    private String messageContent;

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must be at most 500 characters")
    private String reason;

    private Boolean isResolved;
    private Date createdAt;
    private Date resolvedAt;
    private ActionType actionTaken;
    private String actionReason;
}
