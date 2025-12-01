package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.config.DataSeeder;
import com.vizako.anonphobease.config.MongoConfig;
import com.vizako.anonphobease.model.Message;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(MongoConfig.class)
@ActiveProfiles("test")
class MessageRepositoryTest {

    @MockitoBean
    private DataSeeder dataSeeder;

    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        ObjectId userId = new ObjectId();
        ObjectId chatId = new ObjectId();

        Message message = new Message();
        message.setUserId(userId);
        message.setChatId(chatId);
        message.setContent("Hello");

        Message saved = messageRepository.save(message);

        Optional<Message> found = messageRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Hello", found.get().getContent());
        assertEquals(userId, found.get().getUserId());
        assertEquals(chatId, found.get().getChatId());
    }

    @Test
    void updateMessageContent() {
        ObjectId userId = new ObjectId();
        ObjectId chatId = new ObjectId();

        Message message = new Message();
        message.setUserId(userId);
        message.setChatId(chatId);
        message.setContent("Old content");

        Message saved = messageRepository.save(message);

        saved.setContent("New content");
        Message updated = messageRepository.save(saved);

        Optional<Message> found = messageRepository.findById(updated.getId());

        assertTrue(found.isPresent());
        assertEquals("New content", found.get().getContent());
    }

    @Test
    void deleteMessage() {
        ObjectId userId = new ObjectId();
        ObjectId chatId = new ObjectId();

        Message message = new Message();
        message.setUserId(userId);
        message.setChatId(chatId);
        message.setContent("To delete");

        Message saved = messageRepository.save(message);

        messageRepository.deleteById(saved.getId());

        Optional<Message> found = messageRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void findByChatId_returnsOnlyMessagesFromThatChat() {
        ObjectId chat1 = new ObjectId();
        ObjectId chat2 = new ObjectId();
        ObjectId user1 = new ObjectId();
        ObjectId user2 = new ObjectId();

        Message msg1 = new Message();
        msg1.setUserId(user1);
        msg1.setChatId(chat1);
        msg1.setContent("Message 1 in chat1");

        Message msg2 = new Message();
        msg2.setUserId(user2);
        msg2.setChatId(chat1);
        msg2.setContent("Message 2 in chat1");

        Message msg3 = new Message();
        msg3.setUserId(user1);
        msg3.setChatId(chat2);
        msg3.setContent("Message in chat2");

        messageRepository.save(msg1);
        messageRepository.save(msg2);
        messageRepository.save(msg3);

        List<Message> result = messageRepository.findByChatId(chat1);

        assertEquals(2, result.size());
        List<String> contents = result.stream().map(Message::getContent).toList();
        assertTrue(contents.contains("Message 1 in chat1"));
        assertTrue(contents.contains("Message 2 in chat1"));
    }

    @Test
    void findByUserId_returnsOnlyMessagesFromThatUser() {
        ObjectId user1 = new ObjectId();
        ObjectId user2 = new ObjectId();
        ObjectId chat1 = new ObjectId();
        ObjectId chat2 = new ObjectId();

        Message msg1 = new Message();
        msg1.setUserId(user1);
        msg1.setChatId(chat1);
        msg1.setContent("User1 message 1");

        Message msg2 = new Message();
        msg2.setUserId(user1);
        msg2.setChatId(chat2);
        msg2.setContent("User1 message 2");

        Message msg3 = new Message();
        msg3.setUserId(user2);
        msg3.setChatId(chat1);
        msg3.setContent("User2 message");

        messageRepository.save(msg1);
        messageRepository.save(msg2);
        messageRepository.save(msg3);

        List<Message> result = messageRepository.findByUserId(user1);

        assertEquals(2, result.size());
        List<String> contents = result.stream().map(Message::getContent).toList();
        assertTrue(contents.contains("User1 message 1"));
        assertTrue(contents.contains("User1 message 2"));
    }

    @Test
    void findByChatId_whenNoMessages_returnsEmptyList() {
        ObjectId chatId = new ObjectId();

        List<Message> result = messageRepository.findByChatId(chatId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserId_whenNoMessages_returnsEmptyList() {
        ObjectId userId = new ObjectId();

        List<Message> result = messageRepository.findByUserId(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void createdAt_isSetOnSave() {
        ObjectId userId = new ObjectId();
        ObjectId chatId = new ObjectId();

        Message message = new Message();
        message.setUserId(userId);
        message.setChatId(chatId);
        message.setContent("With createdAt");

        Message saved = messageRepository.save(message);

        Date createdAt = saved.getCreatedAt();
        assertNotNull(createdAt);
    }
}
