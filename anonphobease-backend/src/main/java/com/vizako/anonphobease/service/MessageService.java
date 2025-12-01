package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Message;
import com.vizako.anonphobease.repository.MessageRepository;
import com.vizako.anonphobease.util.EncryptionService;
import com.vizako.anonphobease.v1.dto.MessageDTO;
import com.vizako.anonphobease.v1.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final EncryptionService encryptionService;

    public List<MessageDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(this::toDecryptedDto)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> findByChatId(String chatId) {
        ObjectId chatObjectId = new ObjectId(chatId);
        return messageRepository.findByChatId(chatObjectId).stream()
                .map(this::toDecryptedDto)
                .collect(Collectors.toList());
    }

    public Optional<MessageDTO> findById(String id) {
        return messageRepository.findById(new ObjectId(id))
                .map(this::toDecryptedDto);
    }

    public MessageDTO save(MessageDTO dto) {
        Message entity = new Message();
        MessageMapper.updateEntityFromDto(dto, entity);

        if (dto.getContent() != null) {
            entity.setContent(encryptionService.encrypt(dto.getContent()));
        }

        Message saved = messageRepository.save(entity);
        return toDecryptedDto(saved);
    }

    public Optional<MessageDTO> update(String id, MessageDTO dto) {
        if (dto == null) return Optional.empty();
        return messageRepository.findById(new ObjectId(id)).map(existingMessage -> {
            MessageMapper.updateEntityFromDto(dto, existingMessage);

            if (dto.getContent() != null) {
                existingMessage.setContent(encryptionService.encrypt(dto.getContent()));
            }

            Message saved = messageRepository.save(existingMessage);
            return toDecryptedDto(saved);
        });
    }

    public void deleteById(String id) {
        messageRepository.deleteById(new ObjectId(id));
    }

    private MessageDTO toDecryptedDto(Message entity) {
        MessageDTO dto = MessageMapper.toDTO(entity);
        if (entity.getContent() != null) {
            dto.setContent(encryptionService.decrypt(entity.getContent()));
        }
        return dto;
    }
}
