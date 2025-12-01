package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.config.DataSeeder;
import com.vizako.anonphobease.config.MongoConfig;
import com.vizako.anonphobease.model.UserLog;
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
class UserLogRepositoryTest {

    @MockitoBean
    private DataSeeder dataSeeder;

    @Autowired
    private UserLogRepository userLogRepository;

    @BeforeEach
    void setup() {
        userLogRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        ObjectId userId = new ObjectId();

        UserLog log = new UserLog();
        log.setUserId(userId);
        log.setDetails("User logged in");

        UserLog saved = userLogRepository.save(log);

        Optional<UserLog> found = userLogRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(userId, found.get().getUserId());
        assertEquals("User logged in", found.get().getDetails());
    }

    @Test
    void updateLogDetails() {
        ObjectId userId = new ObjectId();

        UserLog log = new UserLog();
        log.setUserId(userId);
        log.setDetails("Old details");

        UserLog saved = userLogRepository.save(log);

        saved.setDetails("Updated details");
        UserLog updated = userLogRepository.save(saved);

        Optional<UserLog> found = userLogRepository.findById(updated.getId());

        assertTrue(found.isPresent());
        assertEquals("Updated details", found.get().getDetails());
    }

    @Test
    void deleteLog() {
        ObjectId userId = new ObjectId();

        UserLog log = new UserLog();
        log.setUserId(userId);
        log.setDetails("To delete");

        UserLog saved = userLogRepository.save(log);

        userLogRepository.deleteById(saved.getId());

        Optional<UserLog> found = userLogRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void findByUserId_returnsOnlyLogsForThatUser() {
        ObjectId user1 = new ObjectId();
        ObjectId user2 = new ObjectId();

        UserLog log1 = new UserLog();
        log1.setUserId(user1);
        log1.setDetails("User1 log 1");

        UserLog log2 = new UserLog();
        log2.setUserId(user1);
        log2.setDetails("User1 log 2");

        UserLog log3 = new UserLog();
        log3.setUserId(user2);
        log3.setDetails("User2 log");

        userLogRepository.save(log1);
        userLogRepository.save(log2);
        userLogRepository.save(log3);

        List<UserLog> result = userLogRepository.findByUserId(user1);

        assertEquals(2, result.size());
        List<String> details = result.stream().map(UserLog::getDetails).toList();
        assertTrue(details.contains("User1 log 1"));
        assertTrue(details.contains("User1 log 2"));
    }

    @Test
    void findByUserId_whenNoLogs_returnsEmptyList() {
        ObjectId userId = new ObjectId();

        List<UserLog> result = userLogRepository.findByUserId(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void createdAt_isSetOnSave() {
        ObjectId userId = new ObjectId();

        UserLog log = new UserLog();
        log.setUserId(userId);
        log.setDetails("Check createdAt");

        UserLog saved = userLogRepository.save(log);

        Date createdAt = saved.getCreatedAt();
        assertNotNull(createdAt);
    }
}
