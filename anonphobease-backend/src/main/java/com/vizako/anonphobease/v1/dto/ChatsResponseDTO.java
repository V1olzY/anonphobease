package com.vizako.anonphobease.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatsResponseDTO {
    private List<ChatDTO> chats;
    private List<PhobiaDTO> phobias;
    private List<LanguageDTO> languages;
}