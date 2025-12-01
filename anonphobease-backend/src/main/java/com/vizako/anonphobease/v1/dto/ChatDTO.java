package com.vizako.anonphobease.v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private String id;
    private String languageId;
    private String languageName;
    private String languageCode;
    private String phobiaId;
    private String phobiaName;
    private String chatName;
}
