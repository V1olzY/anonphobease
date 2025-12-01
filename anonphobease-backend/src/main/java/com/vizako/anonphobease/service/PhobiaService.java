package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.Phobia;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.repository.PhobiaRepository;
import com.vizako.anonphobease.v1.dto.PhobiaDTO;
import com.vizako.anonphobease.v1.mapper.PhobiaMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhobiaService {

    private final PhobiaRepository phobiaRepository;
    private UserLogService userLogService;

    @Autowired
    public void setUserLogService(@Lazy UserLogService userLogService) {
        this.userLogService = userLogService;
    }


    public List<PhobiaDTO> findAll() {
        return phobiaRepository.findAll().stream()
                .map(PhobiaMapper::toDTO)
                .toList();
    }

    public Optional<PhobiaDTO> findById(String id) {
        return phobiaRepository.findById(new ObjectId(id))
                .map(PhobiaMapper::toDTO);
    }

    public PhobiaDTO save(PhobiaDTO dto, String createdByUserId) {
        Phobia entity = PhobiaMapper.toEntity(dto);
        Phobia saved = phobiaRepository.save(entity);
        userLogService.save(createdByUserId, LogType.PHOBIA_SAVE, saved.getId().toHexString(), RelatedEntityType.PHOBIA);
        return PhobiaMapper.toDTO(saved);
    }

    public Optional<PhobiaDTO> update(String id, PhobiaDTO dto, String updatedByUserId) {
        if (dto == null) return Optional.empty();
        return phobiaRepository.findById(new ObjectId(id)).map(existingPhobia -> {
            PhobiaMapper.updateEntityFromDto(dto, existingPhobia);
            Phobia saved = phobiaRepository.save(existingPhobia);
            userLogService.save(updatedByUserId, LogType.PHOBIA_UPDATE, saved.getId().toHexString(), RelatedEntityType.PHOBIA);
            return PhobiaMapper.toDTO(saved);
        });
    }

    public void deleteById(String id, String deletedByUserId) {
        phobiaRepository.deleteById(new ObjectId(id));
        userLogService.save(deletedByUserId, LogType.PHOBIA_DELETE, id, RelatedEntityType.PHOBIA);
    }
}
