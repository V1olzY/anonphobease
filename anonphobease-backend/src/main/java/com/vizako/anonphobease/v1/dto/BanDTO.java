package com.vizako.anonphobease.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanDTO {
    private String id;

    @NotBlank(message = "User id is required")
    private String userId;

    private String username;

    @NotBlank(message = "Chat id is required")
    private String chatId;

    private String chatName;

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must be at most 500 characters")
    private String banReason;

    private String messageId;
    private String messageContent;

    @NotBlank(message = "Moderator id is required")
    private String moderatorId;

    private String moderatorName;

    private Date bannedAt;
}
