package com.vizako.anonphobease.service;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.Phobia;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.repository.PhobiaRepository;
import com.vizako.anonphobease.v1.dto.PhobiaDTO;
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
class PhobiaServiceTest {

    @Mock
    private PhobiaRepository phobiaRepository;

    @Mock
    private UserLogService userLogService;

    @InjectMocks
    private PhobiaService phobiaService;

    @BeforeEach
    void setup() {
        phobiaService.setUserLogService(userLogService);
    }

    @Test
    void findAll_returnsMappedDtos() {
        Phobia p1 = new Phobia();
        p1.setId(new ObjectId());
        p1.setName("Social phobia");
        p1.setDescription("Fear of social situations");

        Phobia p2 = new Phobia();
        p2.setId(new ObjectId());
        p2.setName("Arachnophobia");
        p2.setDescription("Fear of spiders");

        when(phobiaRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PhobiaDTO> result = phobiaService.findAll();

        assertEquals(2, result.size());

        PhobiaDTO dto1 = result.get(0);
        PhobiaDTO dto2 = result.get(1);

        assertEquals(p1.getId().toHexString(), dto1.getId());
        assertEquals("Social phobia", dto1.getName());
        assertEquals("Fear of social situations", dto1.getDescription());

        assertEquals(p2.getId().toHexString(), dto2.getId());
        assertEquals("Arachnophobia", dto2.getName());
        assertEquals("Fear of spiders", dto2.getDescription());
    }

    @Test
    void findById_returnsDtoWhenExists() {
        ObjectId id = new ObjectId();

        Phobia phobia = new Phobia();
        phobia.setId(id);
        phobia.setName("Social phobia");
        phobia.setDescription("Fear of social situations");

        when(phobiaRepository.findById(id)).thenReturn(Optional.of(phobia));

        Optional<PhobiaDTO> result = phobiaService.findById(id.toHexString());

        assertTrue(result.isPresent());
        PhobiaDTO dto = result.get();
        assertEquals(id.toHexString(), dto.getId());
        assertEquals("Social phobia", dto.getName());
        assertEquals("Fear of social situations", dto.getDescription());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        ObjectId id = new ObjectId();

        when(phobiaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<PhobiaDTO> result = phobiaService.findById(id.toHexString());

        assertTrue(result.isEmpty());
    }

    @Test
    void save_savesPhobiaAndCreatesLog() {
        String userId = new ObjectId().toHexString();

        PhobiaDTO dto = new PhobiaDTO();
        dto.setId(null);
        dto.setName("Social phobia");
        dto.setDescription("Fear of social situations");

        Phobia savedEntity = new Phobia();
        ObjectId savedId = new ObjectId();
        savedEntity.setId(savedId);
        savedEntity.setName("Social phobia");
        savedEntity.setDescription("Fear of social situations");

        when(phobiaRepository.save(any(Phobia.class))).thenReturn(savedEntity);

        PhobiaDTO result = phobiaService.save(dto, userId);

        assertEquals(savedId.toHexString(), result.getId());
        assertEquals("Social phobia", result.getName());
        assertEquals("Fear of social situations", result.getDescription());

        verify(userLogService).save(
                eq(userId),
                eq(LogType.PHOBIA_SAVE),
                eq(savedId.toHexString()),
                eq(RelatedEntityType.PHOBIA)
        );
    }

    @Test
    void update_whenPhobiaExists_updatesAndCreatesLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        Phobia existing = new Phobia();
        existing.setId(id);
        existing.setName("Old name");
        existing.setDescription("Old description");

        PhobiaDTO dto = new PhobiaDTO();
        dto.setId(idStr);
        dto.setName("New name");
        dto.setDescription("New description");

        Phobia savedAfterUpdate = new Phobia();
        savedAfterUpdate.setId(id);
        savedAfterUpdate.setName("New name");
        savedAfterUpdate.setDescription("New description");

        when(phobiaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(phobiaRepository.save(any(Phobia.class))).thenReturn(savedAfterUpdate);

        Optional<PhobiaDTO> result = phobiaService.update(idStr, dto, userId);

        assertTrue(result.isPresent());
        PhobiaDTO updated = result.get();
        assertEquals(idStr, updated.getId());
        assertEquals("New name", updated.getName());
        assertEquals("New description", updated.getDescription());

        verify(userLogService).save(
                eq(userId),
                eq(LogType.PHOBIA_UPDATE),
                eq(idStr),
                eq(RelatedEntityType.PHOBIA)
        );
    }

    @Test
    void update_whenDtoIsNull_returnsEmptyAndDoesNothing() {
        String idStr = new ObjectId().toHexString();
        String userId = new ObjectId().toHexString();

        Optional<PhobiaDTO> result = phobiaService.update(idStr, null, userId);

        assertTrue(result.isEmpty());
        verify(phobiaRepository, never()).findById(any());
        verify(phobiaRepository, never()).save(any());
        verify(userLogService, never()).save(anyString(), any(), anyString(), any());
    }

    @Test
    void update_whenPhobiaNotFound_returnsEmptyAndNoLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        PhobiaDTO dto = new PhobiaDTO();
        dto.setId(idStr);
        dto.setName("New name");
        dto.setDescription("New description");

        when(phobiaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<PhobiaDTO> result = phobiaService.update(idStr, dto, userId);

        assertTrue(result.isEmpty());
        verify(phobiaRepository, never()).save(any());
        verify(userLogService, never()).save(anyString(), any(), anyString(), any());
    }

    @Test
    void deleteById_deletesPhobiaAndCreatesLog() {
        ObjectId id = new ObjectId();
        String idStr = id.toHexString();
        String userId = new ObjectId().toHexString();

        phobiaService.deleteById(idStr, userId);

        verify(phobiaRepository).deleteById(eq(id));
        verify(userLogService).save(
                eq(userId),
                eq(LogType.PHOBIA_DELETE),
                eq(idStr),
                eq(RelatedEntityType.PHOBIA)
        );
    }
}
