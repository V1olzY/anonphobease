package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.Language;
import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.repository.LanguageRepository;
import com.vizako.anonphobease.v1.dto.LanguageDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageServiceTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private UserLogService userLogService;

    @InjectMocks
    private LanguageService languageService;

    @BeforeEach
    void setup() {
        languageService.setUserLogService(userLogService);
    }

    @Test
    void findAll_returnsMappedDtos() {
        Language lang1 = new Language();
        lang1.setId(new ObjectId());
        lang1.setName("English");
        lang1.setCode("en");

        Language lang2 = new Language();
        lang2.setId(new ObjectId());
        lang2.setName("Russian");
        lang2.setCode("ru");

        when(languageRepository.findAll()).thenReturn(List.of(lang1, lang2));

        List<LanguageDTO> result = languageService.findAll();

        assertEquals(2, result.size());
        LanguageDTO dto1 = result.get(0);
        LanguageDTO dto2 = result.get(1);

        assertEquals(lang1.getId().toHexString(), dto1.getId());
        assertEquals("English", dto1.getName());
        assertEquals("en", dto1.getCode());

        assertEquals(lang2.getId().toHexString(), dto2.getId());
        assertEquals("Russian", dto2.getName());
        assertEquals("ru", dto2.getCode());
    }

    @Test
    void findById_returnsDtoWhenEntityExists() {
        ObjectId id = new ObjectId();

        Language lang = new Language();
        lang.setId(id);
        lang.setName("English");
        lang.setCode("en");

        when(languageRepository.findById(id)).thenReturn(Optional.of(lang));

        Optional<LanguageDTO> result = languageService.findById(id.toHexString());

        assertTrue(result.isPresent());
        LanguageDTO dto = result.get();
        assertEquals(id.toHexString(), dto.getId());
        assertEquals("English", dto.getName());
        assertEquals("en", dto.getCode());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        ObjectId id = new ObjectId();

        when(languageRepository.findById(id)).thenReturn(Optional.empty());

        Optional<LanguageDTO> result = languageService.findById(id.toHexString());

        assertTrue(result.isEmpty());
    }

    @Test
    void save_savesLanguageAndCreatesLog() {
        String userId = new ObjectId().toHexString();

        LanguageDTO dto = new LanguageDTO();
        dto.setId(null);
        dto.setName("English");
        dto.setCode("en");

        Language savedEntity = new Language();
        ObjectId savedId = new ObjectId();
        savedEntity.setId(savedId);
        savedEntity.setName("English");
        savedEntity.setCode("en");

        when(languageRepository.save(any(Language.class))).thenReturn(savedEntity);

        LanguageDTO result = languageService.save(dto, userId);

        assertEquals(savedId.toHexString(), result.getId());
        assertEquals("English", result.getName());
        assertEquals("en", result.getCode());

        verify(userLogService).save(
                eq(userId),
                eq(LogType.LANGUAGE_SAVE),
                eq(savedId.toHexString()),
                eq(RelatedEntityType.LANGUAGE)
        );
    }

    @Test
    void update_whenLanguageExists_updatesAndCreatesLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        Language existing = new Language();
        existing.setId(id);
        existing.setName("Old name");
        existing.setCode("old");

        LanguageDTO dto = new LanguageDTO();
        dto.setId(idStr);
        dto.setName("New name");
        dto.setCode("new");

        Language savedAfterUpdate = new Language();
        savedAfterUpdate.setId(id);
        savedAfterUpdate.setName("New name");
        savedAfterUpdate.setCode("new");

        when(languageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(languageRepository.save(any(Language.class))).thenReturn(savedAfterUpdate);

        Optional<LanguageDTO> result = languageService.update(idStr, dto, userId);

        assertTrue(result.isPresent());
        LanguageDTO updatedDto = result.get();
        assertEquals(idStr, updatedDto.getId());
        assertEquals("New name", updatedDto.getName());
        assertEquals("new", updatedDto.getCode());

        verify(userLogService).save(
                eq(userId),
                eq(LogType.LANGUAGE_UPDATE),
                eq(idStr),
                eq(RelatedEntityType.LANGUAGE)
        );
    }

    @Test
    void update_whenDtoIsNull_returnsEmptyAndDoesNothing() {
        String idStr = new ObjectId().toHexString();
        String userId = new ObjectId().toHexString();

        Optional<LanguageDTO> result = languageService.update(idStr, null, userId);

        assertTrue(result.isEmpty());
        verify(languageRepository, never()).findById(any());
        verify(languageRepository, never()).save(any());
        verify(userLogService, never()).save(anyString(), any(), anyString(), any());
    }

    @Test
    void update_whenLanguageNotFound_returnsEmptyAndDoesNotLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        LanguageDTO dto = new LanguageDTO();
        dto.setId(idStr);
        dto.setName("New name");
        dto.setCode("new");

        when(languageRepository.findById(id)).thenReturn(Optional.empty());

        Optional<LanguageDTO> result = languageService.update(idStr, dto, userId);

        assertTrue(result.isEmpty());
        verify(languageRepository, never()).save(any());
        verify(userLogService, never()).save(anyString(), any(), anyString(), any());
    }

    @Test
    void deleteById_deletesLanguageAndCreatesLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        languageService.deleteById(idStr, userId);

        verify(languageRepository).deleteById(eq(id));
        verify(userLogService).save(
                eq(userId),
                eq(LogType.LANGUAGE_SAVE),
                eq(idStr),
                eq(RelatedEntityType.LANGUAGE)
        );
    }
}
