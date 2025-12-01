package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Ban;
import com.vizako.anonphobease.v1.dto.BanDTO;
import com.vizako.anonphobease.v1.mapper.BanMapper;
import com.vizako.anonphobease.repository.BanRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BanService {

    private final BanRepository banRepository;

    private final UserService userService;

    private final MessageService messageService;

    private final ChatService chatService;

    public List<BanDTO> findAll() {
        return banRepository.findAll().stream()
                .map(this::toFullDTO)
                .collect(Collectors.toList());
    }

    public boolean isUserGloballyBanned(ObjectId userId) {
        return !banRepository.findByUserId(userId).isEmpty();
    }

    public Optional<BanDTO> findById(String id) {
        if (id == null || !ObjectId.isValid(id)) return Optional.empty();
        return banRepository.findById(new ObjectId(id))
                .map(BanMapper::toDTO);
    }

    public BanDTO save(BanDTO dto) {
        Ban entity = BanMapper.toEntity(dto);
        Ban saved = banRepository.save(entity);
        return BanMapper.toDTO(saved);
    }

    public Optional<BanDTO> update(String id, BanDTO dto) {
        if (dto == null) return Optional.empty();
        return banRepository.findById(new ObjectId(id)).map(existingBan -> {
            BanMapper.updateEntityFromDto(dto, existingBan);
            Ban saved = banRepository.save(existingBan);
            return BanMapper.toDTO(saved);
        });
    }

    public void deleteById(String id) {
        if (id != null && ObjectId.isValid(id)) {
            banRepository.deleteById(new ObjectId(id));
        }
    }

    public BanDTO toFullDTO(Ban ban) {
        if (ban == null) return null;

        BanDTO dto = BanMapper.toDTO(ban);

        // resolve user name
        if (ban.getUserId() != null) {
            dto.setUserId(ban.getUserId().toString());
            userService.findById(ban.getUserId().toString()).ifPresent(u -> dto.setUsername(u.getUsername()));
        }

        // resolve moderator name
        if (ban.getModeratorId() != null) {
            dto.setModeratorId(ban.getModeratorId().toString());
            userService.findById(ban.getModeratorId().toString())
                    .ifPresent(u -> dto.setModeratorName(u.getUsername()));
        }

        if (ban.getMessageId() != null) {
            dto.setMessageId(ban.getMessageId().toString());
            messageService.findById(ban.getMessageId().toString())
                    .ifPresent(m -> dto.setMessageContent(m.getContent()));
        }

        if (ban.getChatId() != null) {
            dto.setChatId(ban.getChatId().toHexString());
            String chatName = chatService.findById(ban.getChatId()).map(c -> c.getChatName()).orElse(null);
            dto.setChatName(chatName);
        }

        return dto;
    }

}