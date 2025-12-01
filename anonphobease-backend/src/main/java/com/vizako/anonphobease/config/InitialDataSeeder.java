package com.vizako.anonphobease.config;

import com.vizako.anonphobease.model.*;
import com.vizako.anonphobease.repository.*;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("docker")
public class InitialDataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PhobiaRepository phobiaRepository;
    private final ChatRepository chatRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        // Roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(new ObjectId(), "ADMIN")));
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role(new ObjectId(), "USER")));
        Role modRole = roleRepository.findByName("MODERATOR")
                .orElseGet(() -> roleRepository.save(new Role(new ObjectId(), "MODERATOR")));

        // Languages
        Language est = languageRepository.findByCode("est")
                .orElseGet(() -> languageRepository.save(
                        new Language(new ObjectId(), "Eesti", "est")
                ));
        Language rus = languageRepository.findByCode("rus")
                .orElseGet(() -> languageRepository.save(
                        new Language(new ObjectId(), "Русский", "rus")
                ));
        Language eng = languageRepository.findByCode("eng")
                .orElseGet(() -> languageRepository.save(
                        new Language(new ObjectId(), "English", "eng")
                ));

        var languages = List.of(est, rus, eng);

        // Phobias
        if (phobiaRepository.count() == 0) {
            phobiaRepository.saveAll(List.of(
                    new Phobia(new ObjectId(), "AGORAPHOBIA",
                            "Fear of open or crowded places."),
                    new Phobia(new ObjectId(), "SOCIAL_PHOBIA",
                            "Fear of social situations and being judged by others."),
                    new Phobia(new ObjectId(), "ACROPHOBIA",
                            "Fear of heights."),
                    new Phobia(new ObjectId(), "CLAUSTROPHOBIA",
                            "Fear of confined or enclosed spaces."),
                    new Phobia(new ObjectId(), "ARACHNOPHOBIA",
                            "Fear of spiders."),
                    new Phobia(new ObjectId(), "OPHIDIOPHOBIA",
                            "Fear of snakes."),
                    new Phobia(new ObjectId(), "AEROPHOBIA",
                            "Fear of flying."),
                    new Phobia(new ObjectId(), "TRYPOPHOBIA",
                            "Discomfort or fear triggered by clusters of small holes or patterns."),
                    new Phobia(new ObjectId(), "MYSOPHOBIA",
                            "Fear of germs, dirt or contamination."),
                    new Phobia(new ObjectId(), "HEMOPHOBIA",
                            "Fear of blood."),
                    new Phobia(new ObjectId(), "CARCINOPHOBIA",
                            "Fear of cancer."),
                    new Phobia(new ObjectId(), "THANATOPHOBIA",
                            "Fear of death or dying."),
                    new Phobia(new ObjectId(), "EMETOPHOBIA",
                            "Fear of vomiting."),
                    new Phobia(new ObjectId(), "NYCTOPHOBIA",
                            "Fear of the dark."),
                    new Phobia(new ObjectId(), "GLOSSOPHOBIA",
                            "Fear of public speaking.")
            ));
        }

        var phobias = phobiaRepository.findAll();

        // Chats
        if (chatRepository.count() == 0) {
            for (Phobia phobia : phobias) {
                for (Language lang : languages) {
                    for (int i = 1; i <= 3; i++) {
                        String chatName = "(" + lang.getCode().toUpperCase() + ") "
                                + phobia.getName() + " #" + i;

                        Chat chat = new Chat(
                                new ObjectId(),
                                lang.getId(),
                                phobia.getId(),
                                chatName,
                                new Date()
                        );
                        chatRepository.save(chat);
                    }
                }
            }
        }

        // Admin
        userRepository.findByUsername(adminUsername)
                .orElseGet(() -> {
                    String encoded = passwordEncoder.encode(adminPassword);
                    User admin = User.builder()
                            .id(new ObjectId())
                            .username(adminUsername)
                            .password(encoded)
                            .roleId(adminRole.getId())
                            .isActive(true)
                            .createdAt(new Date())
                            .build();
                    return userRepository.save(admin);
                });
    }
}
