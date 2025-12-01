package com.vizako.anonphobease.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MessageFilterService {


    private final Map<String, Set<String>> bannedWordsByLang = new HashMap<>();

    @PostConstruct
    void init() {
        loadWords("eng", "profanity/bad-words_eng.txt");
        loadWords("est", "profanity/bad-words_est.txt");
        loadWords("rus", "profanity/bad-words_rus.txt");

        System.out.println("[FILTER] Loaded dictionaries:");
        bannedWordsByLang.forEach((lang, words) ->
                System.out.println("  " + lang + " -> " + words.size() + " sõna"));
    }

    private void loadWords(String langCode, String resourcePath) {
        Set<String> words = new HashSet<>();
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                System.out.println("[FILTER] Resource not found: " + resourcePath);
                bannedWordsByLang.put(langCode, words);
                return;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                        words.add(trimmed.toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bannedWordsByLang.put(langCode, words);
    }

    public String filter(String originalText, String languageCode) {
        if (originalText == null || originalText.isBlank()) {
            return originalText;
        }

        Set<String> allWords = new HashSet<>();
        bannedWordsByLang.values().forEach(allWords::addAll);

        System.out.println("[FILTER] languageCode=" + languageCode
                + ", allWords=" + allWords.size());

        if (allWords.isEmpty()) {
            return originalText;
        }

        String filtered = originalText;

        for (String badWord : allWords) {
            String regex = "(?iu)" + Pattern.quote(badWord);
            String stars = "*".repeat(badWord.length());
            filtered = filtered.replaceAll(regex, stars);
        }

        System.out.println("[FILTER] original='" + originalText + "', result='" + filtered + "'");
        return filtered;
    }
}
