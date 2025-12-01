package com.vizako.anonphobease.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
public class CaptchaService {

    @Value("${captcha.secret-key}")
    private String secretKey;

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();

        String url = VERIFY_URL + "?secret=" + secretKey + "&response=" + token;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Boolean success = (Boolean) response.getBody().get("success");
                return success != null && success;
            }
        } catch (Exception e) {
            System.err.println("Error verifying captcha: " + e.getMessage());
        }

        return false;
    }
}
