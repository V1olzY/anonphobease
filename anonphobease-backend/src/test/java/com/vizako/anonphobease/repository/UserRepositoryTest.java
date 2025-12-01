package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.config.DataSeeder;
import com.vizako.anonphobease.config.MongoConfig;
import com.vizako.anonphobease.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(MongoConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @MockitoBean
    private DataSeeder dataSeeder;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void saveAndFind() {
        User user = new User();
        user.setUsername("test");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("test");

        assertTrue(found.isPresent());
    }

    @Test
    void findByUsername_userNotFound() {
        Optional<User> found = userRepository.findByUsername("unknown");
        assertTrue(found.isEmpty());
    }

    @Test
    void findByUsername_returnsCorrectUser() {
        User user1 = new User();
        user1.setUsername("alice");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("bob");
        userRepository.save(user2);

        Optional<User> found = userRepository.findByUsername("bob");

        assertTrue(found.isPresent());
        assertEquals("bob", found.get().getUsername());
    }


    @Test
    void saveDuplicateUsername_throwsDuplicateKeyException() {
        User user1 = new User();
        user1.setUsername("test");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("test");

        assertThrows(DuplicateKeyException.class, () -> userRepository.save(user2));
    }

    @Test
    void deleteUserById() {
        User user = new User();
        user.setUsername("to-delete");
        user = userRepository.save(user);

        userRepository.deleteById(user.getId());

        assertTrue(userRepository.findById(user.getId()).isEmpty());
    }

    @Test
    void createdAt_isSetOnSave() {
        User user = new User();
        user.setUsername("with-created-at");

        User saved = userRepository.save(user);

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findByIsActive_true_returnsOnlyActiveUsers() {
        User active1 = User.builder()
                .username("active1")
                .isActive(true)
                .build();

        User active2 = User.builder()
                .username("active2")
                .isActive(true)
                .build();

        User inactive = User.builder()
                .username("inactive")
                .isActive(false)
                .build();

        userRepository.save(active1);
        userRepository.save(active2);
        userRepository.save(inactive);

        List<User> result = userRepository.findByIsActive(true);

        assertEquals(2, result.size());
        List<String> usernames = result.stream().map(User::getUsername).toList();
        assertTrue(usernames.contains("active1"));
        assertTrue(usernames.contains("active2"));
    }


}
