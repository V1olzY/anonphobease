package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.ChatUser;
import com.vizako.anonphobease.repository.ChatUserRepository;
import com.vizako.anonphobease.v1.dto.ChatUserDTO;
import com.vizako.anonphobease.v1.mapper.ChatUserMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository repository;

    public List<ChatUserDTO> findAll() {
        return repository.findAll().stream()
                .map(ChatUserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ChatUserDTO> findById(ObjectId id) {
        return repository.findById(id)
                .map(ChatUserMapper::toDTO);
    }

    public ChatUserDTO save(ChatUserDTO dto) {
        ChatUser entity = ChatUserMapper.toEntity(dto);
        ChatUser saved = repository.save(entity);
        return ChatUserMapper.toDTO(saved);
    }

    public void deleteById(ObjectId id) {
        repository.deleteById(id);
    }
}
