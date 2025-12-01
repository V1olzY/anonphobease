package com.vizako.anonphobease.v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDTO {
    private String id; private String chatId; private String userId; private Date joinedAt;
}
