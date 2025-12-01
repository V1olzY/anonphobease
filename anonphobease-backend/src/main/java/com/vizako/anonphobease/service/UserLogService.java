package com.vizako.anonphobease.service;

import com.vizako.anonphobease.helper.RelatedEntityHelperService;
import com.vizako.anonphobease.model.*;
import com.vizako.anonphobease.repository.UserLogRepository;
import com.vizako.anonphobease.v1.dto.*;
import com.vizako.anonphobease.v1.mapper.UserLogMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLogService {

    private final UserLogRepository userLogRepository;
    private final UserService userService;
    private final RelatedEntityHelperService relatedEntityHelperService;


public List<UserLogDTO> findAll() {
        return userLogRepository.findAll().stream()
                .map(this::toEnrichedDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserLogDTO> findById(String id) {
        return userLogRepository.findById(new ObjectId(id))
                .map(this::toEnrichedDTO);
    }

    public List<UserLogDTO> findByUserId(String userId) {
        return userLogRepository.findByUserId(new ObjectId(userId)).stream()
                .map(this::toEnrichedDTO)
                .collect(Collectors.toList());
    }

    public UserLogDTO save(String userId, LogType logType, String relatedEntityId, RelatedEntityType relatedEntityType) {
        UserLogDTO dto = new UserLogDTO(
                null,
                userId,
                null,
                logType,
                new Date(),
                logType.getTemplate(),
                relatedEntityId,
                relatedEntityType,
                null,
                null
        );
        UserLog entity = UserLogMapper.toEntity(dto);
        UserLog saved = userLogRepository.save(entity);
        return UserLogMapper.toDTO(saved, dto.getUserName(), null, null);
    }

    public void deleteById(String id) {
        userLogRepository.deleteById(new ObjectId(id));
    }

    private UserLogDTO toEnrichedDTO(UserLog userLog) {
        String userName = userService.getUserNameById(userLog.getUserId().toString());
        RelatedEntityInfo info = relatedEntityHelperService.getRelatedEntityInfo(userLog);
        return UserLogMapper.toDTO(userLog, userName, info.getName(), info.getExtra());
    }
}
