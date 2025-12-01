package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.config.DataSeeder;
import com.vizako.anonphobease.model.Phobia;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class PhobiaRepositoryTest {

    @MockitoBean
    private DataSeeder dataSeeder;

    @Autowired
    private PhobiaRepository phobiaRepository;

    @BeforeEach
    void setup() {
        phobiaRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        Phobia phobia = new Phobia();
        phobia.setName("Social phobia");
        phobia.setDescription("Fear of social situations");

        Phobia saved = phobiaRepository.save(phobia);

        Optional<Phobia> found = phobiaRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Social phobia", found.get().getName());
        assertEquals("Fear of social situations", found.get().getDescription());
    }

    @Test
    void updatePhobia() {
        Phobia phobia = new Phobia();
        phobia.setName("Social phobia");
        phobia.setDescription("Short description");

        Phobia saved = phobiaRepository.save(phobia);

        saved.setDescription("Updated description");
        Phobia updated = phobiaRepository.save(saved);

        Optional<Phobia> found = phobiaRepository.findById(updated.getId());

        assertTrue(found.isPresent());
        assertEquals("Updated description", found.get().getDescription());
    }

    @Test
    void deletePhobia() {
        Phobia phobia = new Phobia();
        phobia.setName("Test phobia");
        phobia.setDescription("To delete");

        Phobia saved = phobiaRepository.save(phobia);

        phobiaRepository.deleteById(saved.getId());

        Optional<Phobia> found = phobiaRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void findByName_returnsPhobiaWhenExists() {
        Phobia phobia = new Phobia();
        phobia.setName("Social phobia");
        phobia.setDescription("Fear of social situations");

        phobiaRepository.save(phobia);

        Optional<Phobia> found = phobiaRepository.findByName("Social phobia");

        assertTrue(found.isPresent());
        assertEquals("Social phobia", found.get().getName());
    }

    @Test
    void findByName_returnsEmptyWhenNotExists() {
        Optional<Phobia> found = phobiaRepository.findByName("Unknown");
        assertTrue(found.isEmpty());
    }

    @Test
    void saveDuplicateName_throwsDuplicateKeyException() {
        Phobia phobia1 = new Phobia();
        phobia1.setName("Social phobia");
        phobia1.setDescription("Desc 1");

        Phobia phobia2 = new Phobia();
        phobia2.setName("Social phobia");
        phobia2.setDescription("Desc 2");

        phobiaRepository.save(phobia1);

        assertThrows(DuplicateKeyException.class, () -> phobiaRepository.save(phobia2));
    }

    @Test
    void findAll_returnsAllPhobias() {
        Phobia phobia1 = new Phobia();
        phobia1.setName("Social phobia");
        phobia1.setDescription("Desc 1");

        Phobia phobia2 = new Phobia();
        phobia2.setName("Arachnophobia");
        phobia2.setDescription("Fear of spiders");

        phobiaRepository.save(phobia1);
        phobiaRepository.save(phobia2);

        List<Phobia> all = phobiaRepository.findAll();

        assertEquals(2, all.size());
    }
}
