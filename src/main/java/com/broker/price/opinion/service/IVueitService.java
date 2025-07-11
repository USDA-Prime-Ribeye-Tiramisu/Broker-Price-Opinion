package com.broker.price.opinion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class IVueitService {

    @Value("${ivueit.api.api.key}")
    private String iVueitAPIapiKey;

    @Value("${ivueit.api.secret}")
    private String iVueitAPISecret;

    private final JdbcTemplate prodJdbcTemplate;
    private final JdbcTemplate prodBackupJdbcTemplate;

    @Autowired
    public IVueitService(
            @Qualifier("prodJdbcTemplate") JdbcTemplate prodJdbcTemplate,
            @Qualifier("prodBackupJdbcTemplate") JdbcTemplate prodBackupJdbcTemplate) {
        this.prodJdbcTemplate = prodJdbcTemplate;
        this.prodBackupJdbcTemplate = prodBackupJdbcTemplate;
    }

    public String getAccessTokenIVueitAPI() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("apiKey", iVueitAPIapiKey);
        requestBody.put("secret", iVueitAPISecret);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.staging.ivueit.services/login/v1/service",
                    HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode root = objectMapper.readTree(response.getBody());
                    return root.path("body").asText();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse Authentication Response", e);
                }
            } else {
                throw new RuntimeException("Authentication Failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Authentication Exception: " + e.getMessage(), e);
        }
    }
}