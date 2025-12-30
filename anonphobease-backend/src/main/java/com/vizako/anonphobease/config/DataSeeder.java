package com.vizako.anonphobease.config;

import com.vizako.anonphobease.model.*;
import com.vizako.anonphobease.repository.*;
import com.vizako.anonphobease.repository.UserLogRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Date;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PhobiaRepository phobiaRepository;
    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final MessageRepository messageRepository;
    private final ReportRepository reportRepository;
    private final BanRepository banRepository;
    private final UserLogRepository userLogRepository;

    @Override
    public void run(String... args) throws Exception {

        userLogRepository.deleteAll();
        banRepository.deleteAll();
        reportRepository.deleteAll();
        messageRepository.deleteAll();
        chatUserRepository.deleteAll();
        chatRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        languageRepository.deleteAll();
        phobiaRepository.deleteAll();

        // Roles
        Role userRole = roleRepository.save(new Role(new ObjectId(), "USER"));
        Role modRole = roleRepository.save(new Role(new ObjectId(), "MODERATOR"));
        Role adminRole = roleRepository.save(new Role(new ObjectId(), "ADMIN"));

        // Languages
        Language est = languageRepository.save(new Language(new ObjectId(), "Eesti", "est"));
        Language rus = languageRepository.save(new Language(new ObjectId(), "Русский", "rus"));

        // Phobias
        Phobia spider = phobiaRepository.save(new Phobia(new ObjectId(), "Arachnophobia", "Fear of spiders"));
        Phobia height = phobiaRepository.save(new Phobia(new ObjectId(), "Acrophobia", "Fear of heights"));

        // Users
        User user1 = userRepository.save(User.builder()
                .id(new ObjectId())
                .username("user1")
                .password(null)
                .roleId(userRole.getId())
                .isActive(false)
                .createdAt(new Date())
                .build());

        User user2 = userRepository.save(User.builder()
                .id(new ObjectId())
                .username("user2")
                .password(null)
                .roleId(userRole.getId())
                .isActive(false)
                .createdAt(new Date())
                .build());

        User mod = userRepository.save(User.builder()
                .id(new ObjectId())
                .username("moderator1")
                .password("$2a$12$9tqpq6oCKen1xV0Z9ufKYeAaHWCV/vFoDpGEJIljcl3xMEFh79wXq")
                .roleId(modRole.getId())
                .isActive(false)
                .createdAt(new Date())
                .build());

        User admin = userRepository.save(User.builder()
                .id(new ObjectId())
                .username("admin")
                .password("$2a$12$9tqpq6oCKen1xV0Z9ufKYeAaHWCV/vFoDpGEJIljcl3xMEFh79wXq")
                .roleId(adminRole.getId())
                .isActive(false)
                .createdAt(new Date())
                .build());

        // Chat
        Chat chat = chatRepository.save(new Chat(new ObjectId(), est.getId(), spider.getId(), "(" + est.getCode().toUpperCase() + ") " + spider.getName(), new Date()));

        // Add user to chat
        ChatUser chatUser = chatUserRepository.save(new ChatUser(new ObjectId(), chat.getId(), user1.getId(), new Date(), null));

        // Message
        Message msg = messageRepository.save(new Message(new ObjectId(), user1.getId(), chat.getId(), "Hello! Anyone else afraid of spiders?", new Date()));

        // UserLog
        userLogRepository.save(new UserLog(new ObjectId(), user1.getId(), LogType.USER_LOGIN, new Date(), LogType.USER_LOGIN.getTemplate(), null, RelatedEntityType.USER));
    }
}
