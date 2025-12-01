package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Chat;
import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.repository.ChatRepository;
import com.vizako.anonphobease.v1.dto.ChatDTO;
import com.vizako.anonphobease.v1.dto.ChatsResponseDTO;
import com.vizako.anonphobease.v1.dto.LanguageDTO;
import com.vizako.anonphobease.v1.dto.PhobiaDTO;
import com.vizako.anonphobease.v1.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {


    private final ChatRepository chatRepository;

    private final LanguageService languageService;

    private final PhobiaService phobiaService;

    private UserLogService userLogService;

    @Autowired
    public void setUserLogService(@Lazy UserLogService userLogService) {
        this.userLogService = userLogService;
    }

    public List<ChatDTO> findAll() {
        return chatRepository.findAll()
            .stream()
            .map(ChatMapper::toDTO)
            .toList();
    }

    public ChatsResponseDTO getAllChatsWithFilters() {
        List<Chat> chats = chatRepository.findAll();
        List<LanguageDTO> languageDTOs = languageService.findAll();
        List<PhobiaDTO> phobiaDTOs = phobiaService.findAll();

        List<ChatDTO> chatDTOs = chats.stream().map(chat -> {
            LanguageDTO langDTO = languageDTOs.stream()
                    .filter(l -> l.getId().equals(chat.getLanguageId().toHexString()))
                    .findFirst().orElse(null);
            PhobiaDTO phobiaDTO = phobiaDTOs.stream()
                    .filter(p -> p.getId().equals(chat.getPhobiaId().toHexString()))
                    .findFirst().orElse(null);

            return new ChatDTO(
                    chat.getId().toHexString(),
                    chat.getLanguageId().toHexString(),
                    langDTO != null ? langDTO.getName() : null,
                    langDTO!= null ? langDTO.getCode() : null,
                    chat.getPhobiaId().toHexString(),
                    phobiaDTO != null ? phobiaDTO.getName() : null,
                    chat.getName()
            );
        }).toList();

        return new ChatsResponseDTO(chatDTOs, phobiaDTOs, languageDTOs);
    }

    public String getLanguageCodeByChatId(String chatId) {
        return findById(new ObjectId(chatId))
                .flatMap(chatDto -> languageService.findById(chatDto.getLanguageId()))
                .map(LanguageDTO::getCode)
                .orElse("UNK");
    }



    public Optional<ChatDTO> findById(ObjectId id) {
        return chatRepository.findById(id)
                .map(ChatMapper::toDTO);
    }

    public ChatDTO save(ChatDTO dto, String createdByUserId) {
        Chat entity = ChatMapper.toEntity(dto);
        entity.setName(buildChatName(dto.getLanguageId(), dto.getPhobiaId()));
        Chat saved = chatRepository.save(entity);
        userLogService.save(createdByUserId, LogType.CHAT_SAVE, saved.getId().toHexString(), RelatedEntityType.CHAT);
        return ChatMapper.toDTO(saved);
    }

    public Optional<ChatDTO> update(String id, ChatDTO dto, String createdByUserId) {
        if (dto == null) return Optional.empty();
        return chatRepository.findById(new ObjectId(id)).map(existingChat -> {
            ChatMapper.updateEntityFromDto(dto, existingChat);
            existingChat.setName(buildChatName(dto.getLanguageId(), dto.getPhobiaId()));
            Chat saved = chatRepository.save(existingChat);
            userLogService.save(createdByUserId, LogType.CHAT_UPDATE, saved.getId().toHexString(), RelatedEntityType.CHAT);
            return ChatMapper.toDTO(saved);
        });
    }

    public String getChatNameById(String chatId) {
        return findById(new ObjectId(chatId))
                .map(chatDto -> buildChatName(chatDto.getLanguageId(), chatDto.getPhobiaId()))
                .orElse("Unknown Chat");
    }


    private String buildChatName(String languageId, String phobiaId) {
        String languageCode = languageService.findById(languageId)
                .map(LanguageDTO::getCode)
                .map(String::toUpperCase)         // Делаем большие буквы
                .orElse("UNK");

        String phobiaName = phobiaService.findById(phobiaId)
                .map(PhobiaDTO::getName)
                .orElse("Unknown Phobia");

        return "(" + languageCode + ") " + phobiaName;
    }



    public void deleteById(String id, String createdByUserId) {
        userLogService.save(createdByUserId, LogType.CHAT_DELETE, id, RelatedEntityType.CHAT);
        chatRepository.deleteById(new ObjectId(id));
    }
}
