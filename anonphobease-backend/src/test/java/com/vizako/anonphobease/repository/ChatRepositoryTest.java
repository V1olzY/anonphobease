package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.config.DataSeeder;
import com.vizako.anonphobease.model.Chat;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class ChatRepositoryTest {

    @MockitoBean
    private DataSeeder dataSeeder;

    @Autowired
    private ChatRepository chatRepository;

    @BeforeEach
    void setUp() {
        chatRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        Chat chat = new Chat();
        ObjectId languageId = new ObjectId();
        ObjectId phobiaId = new ObjectId();
        chat.setLanguageId(languageId);
        chat.setPhobiaId(phobiaId);

        Chat saved = chatRepository.save(chat);

        Optional<Chat> found = chatRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(languageId, found.get().getLanguageId());
        assertEquals(phobiaId, found.get().getPhobiaId());
    }

    @Test
    void updateChat() {
        Chat chat = new Chat();
        chat.setLanguageId(new ObjectId());
        chat.setPhobiaId(new ObjectId());

        Chat saved = chatRepository.save(chat);
        ObjectId newLanguageId = new ObjectId();
        saved.setLanguageId(newLanguageId);
        Chat updated = chatRepository.save(saved);

        Optional<Chat> found = chatRepository.findById(updated.getId());

        assertTrue(found.isPresent());
        assertEquals(newLanguageId, found.get().getLanguageId());
    }

    @Test
    void deleteChat() {
        Chat chat = new Chat();
        chat.setLanguageId(new ObjectId());
        chat.setPhobiaId(new ObjectId());

        Chat saved = chatRepository.save(chat);

        chatRepository.deleteById(saved.getId());

        Optional<Chat> found = chatRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void findByLanguageId_returnsOnlyChatsWithThatLanguage() {
        ObjectId language1 = new ObjectId();
        ObjectId language2 = new ObjectId();

        Chat chat1 = new Chat();
        chat1.setLanguageId(language1);
        chat1.setPhobiaId(new ObjectId());

        Chat chat2 = new Chat();
        chat2.setLanguageId(language1);
        chat2.setPhobiaId(new ObjectId());

        Chat chat3 = new Chat();
        chat3.setLanguageId(language2);
        chat3.setPhobiaId(new ObjectId());

        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        List<Chat> result = chatRepository.findByLanguageId(language1);

        assertEquals(2, result.size());
    }

    @Test
    void findByPhobiaId_returnsOnlyChatsWithThatPhobia() {
        ObjectId phobia1 = new ObjectId();
        ObjectId phobia2 = new ObjectId();

        Chat chat1 = new Chat();
        chat1.setLanguageId(new ObjectId());
        chat1.setPhobiaId(phobia1);

        Chat chat2 = new Chat();
        chat2.setLanguageId(new ObjectId());
        chat2.setPhobiaId(phobia1);

        Chat chat3 = new Chat();
        chat3.setLanguageId(new ObjectId());
        chat3.setPhobiaId(phobia2);

        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        List<Chat> result = chatRepository.findByPhobiaId(phobia1);

        assertEquals(2, result.size());
    }

    @Test
    void findByLanguageId_whenNoneMatch_returnsEmptyList() {
        ObjectId languageId = new ObjectId();

        Chat chat = new Chat();
        chat.setLanguageId(new ObjectId()); // другой lang
        chat.setPhobiaId(new ObjectId());

        chatRepository.save(chat);

        List<Chat> result = chatRepository.findByLanguageId(languageId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByPhobiaId_whenNoneMatch_returnsEmptyList() {
        ObjectId phobiaId = new ObjectId();

        Chat chat = new Chat();
        chat.setLanguageId(new ObjectId());
        chat.setPhobiaId(new ObjectId()); // другая фобия

        chatRepository.save(chat);

        List<Chat> result = chatRepository.findByPhobiaId(phobiaId);

        assertTrue(result.isEmpty());
    }
}
