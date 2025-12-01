package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Language;
import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.repository.LanguageRepository;
import com.vizako.anonphobease.v1.dto.LanguageDTO;
import com.vizako.anonphobease.v1.mapper.LanguageMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private UserLogService userLogService;

    @Autowired
    public void setUserLogService(@Lazy UserLogService userLogService) {
        this.userLogService = userLogService;
    }


    public List<LanguageDTO> findAll() {
        return languageRepository.findAll().stream()
                .map(LanguageMapper::toDTO)
                .toList();
    }

    public Optional<LanguageDTO> findById(String id) {
        return languageRepository.findById(new ObjectId(id))
            .map(LanguageMapper::toDTO);
    }

    public LanguageDTO save(LanguageDTO dto, String createdByUserId) {
        Language entity = LanguageMapper.toEntity(dto);
        Language saved = languageRepository.save(entity);
        userLogService.save(createdByUserId, LogType.LANGUAGE_SAVE, saved.getId().toHexString(), RelatedEntityType.LANGUAGE);
        return LanguageMapper.toDTO(saved);
    }

    public Optional<LanguageDTO> update(String id, LanguageDTO dto, String createdByUserId) {
        if (dto == null) return Optional.empty();
        return languageRepository.findById(new ObjectId(id)).map(existingLanguage -> {
            LanguageMapper.updateEntityFromDto(dto, existingLanguage);
            Language saved = languageRepository.save(existingLanguage);
            userLogService.save(createdByUserId, LogType.LANGUAGE_UPDATE, saved.getId().toHexString(), RelatedEntityType.LANGUAGE);
            return LanguageMapper.toDTO(saved);

        });
    }

    public void deleteById(String id, String createdByUserId) {
        languageRepository.deleteById(new ObjectId(id));
        userLogService.save(createdByUserId, LogType.LANGUAGE_SAVE, id, RelatedEntityType.LANGUAGE);

    }
}
