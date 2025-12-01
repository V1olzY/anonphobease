package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.config.DataSeeder;
import com.vizako.anonphobease.model.Language;
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
class LanguageRepositoryTest {

    @MockitoBean
    private DataSeeder dataSeeder;

    @Autowired
    private LanguageRepository languageRepository;

    @BeforeEach
    void setup() {
        languageRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        Language language = new Language();
        language.setName("English");
        language.setCode("en");

        Language saved = languageRepository.save(language);

        Optional<Language> found = languageRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("English", found.get().getName());
        assertEquals("en", found.get().getCode());
    }

    @Test
    void updateLanguage() {
        Language language = new Language();
        language.setName("Eng");
        language.setCode("en");

        Language saved = languageRepository.save(language);

        saved.setName("English");
        Language updated = languageRepository.save(saved);

        Optional<Language> found = languageRepository.findById(updated.getId());

        assertTrue(found.isPresent());
        assertEquals("English", found.get().getName());
    }

    @Test
    void deleteLanguage() {
        Language language = new Language();
        language.setName("English");
        language.setCode("en");

        Language saved = languageRepository.save(language);

        languageRepository.deleteById(saved.getId());

        Optional<Language> found = languageRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void findByCode_returnsLanguageWhenExists() {
        Language language = new Language();
        language.setName("English");
        language.setCode("en");

        languageRepository.save(language);

        Optional<Language> found = languageRepository.findByCode("en");

        assertTrue(found.isPresent());
        assertEquals("English", found.get().getName());
    }

    @Test
    void findByCode_returnsEmptyWhenNotExists() {
        Optional<Language> found = languageRepository.findByCode("xx");
        assertTrue(found.isEmpty());
    }

    @Test
    void saveDuplicateCode_throwsDuplicateKeyException() {
        Language lang1 = new Language();
        lang1.setName("English");
        lang1.setCode("en");

        Language lang2 = new Language();
        lang2.setName("Another English");
        lang2.setCode("en");

        languageRepository.save(lang1);

        assertThrows(DuplicateKeyException.class, () -> languageRepository.save(lang2));
    }

    @Test
    void findAll_returnsAllLanguages() {
        Language lang1 = new Language();
        lang1.setName("English");
        lang1.setCode("en");

        Language lang2 = new Language();
        lang2.setName("Russian");
        lang2.setCode("ru");

        languageRepository.save(lang1);
        languageRepository.save(lang2);

        List<Language> all = languageRepository.findAll();

        assertEquals(2, all.size());
    }
}
