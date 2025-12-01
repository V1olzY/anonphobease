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
public class MessageDTO {
    private String messageId;

    @NotBlank(message = "Message content is required")
    @Size(max = 2000, message = "Message content must be at most 2000 characters")
    private String content;

    @NotBlank(message = "User id is required")
    private String userId;

    private String username;
    private String role;

    @NotBlank(message = "Chat id is required")
    private String chatId;

    private Date createdAt;
}
