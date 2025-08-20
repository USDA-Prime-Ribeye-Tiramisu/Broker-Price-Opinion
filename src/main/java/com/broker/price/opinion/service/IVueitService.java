package com.broker.price.opinion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class IVueitService {

    @Value("${ivueit.api.api.key}")
    private String iVueitAPIapiKey;

    @Value("${ivueit.api.secret}")
    private String iVueitAPISecret;

    @Value("${ivueit.api.survey.id}")
    private String iVueitAPISurveyId;

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
                    "https://prod.data.ivueit.network/login/v1/service",
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

    public String createIVueitImagesRequest(String propertyId, String address, String city, String state, String zipcode) {

        int iVueitRequestId = createBPOiVueitServiceUsageRow(propertyId, address, city, state, zipcode);

        String url = "https://prod.data.ivueit.network/api/v1/batch/import";

        long publishAt = getPublishAtNanos();
        long expiresAt = publishAt + Duration.ofDays(7).toMillis() * 1_000_000;

        String requestBody = "{"
                + "\"surveyTemplate\": \"" + iVueitAPISurveyId + "\","
                + "\"score\": 80,"
                + "\"startsAt\": \"08:00\","
                + "\"endsAt\": \"23:59\","
                + "\"publishAt\": " + publishAt + ","
                + "\"expiresAt\": {"
                + "  \"value\": " + expiresAt + ""
                + "},"
                + "\"isInternal\": false,"
                + "\"repeat\": true,"
                + "\"urgent\": true,"
                + "\"selectedDays\": \"{\\\"selectedDays\\\":[0,1,2,3,4,5,6]}\","
                + "\"backgroundCheckRequired\": false,"
                + "\"highQualityRequired\": true,"
                + "\"includePhotoTimestamp\": true,"
                + "\"vueData\": {"
                + "  \"vueName\": \"test-vue-" + iVueitRequestId + "-sl\","
                + "  \"vueNotes\": \"test-note-" + iVueitRequestId + "-sl\","
                + "  \"vueAddress\": \"" + address + "\","
                + "  \"vueCity\": \"" + city + "\","
                + "  \"vueStateOrProvinceTwoCharacterCode\": \"" + state + "\","
                + "  \"vueZipOrPostalCode\": \"" + zipcode + "\","
                + "  \"attachmentFileIds\": [],"
                + "  \"vueCustomMetadata\": {"
                + "    \"test\": \"test-sl-" + iVueitRequestId + "\""
                + "  }"
                + "}"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x_ivueit_auth_token", getAccessTokenIVueitAPI());

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                updateBPOiVueitRequestRowVueId(iVueitRequestId, root.path("vueId").asText());
                return root.path("vueId").asText();
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse batch import response", e);
            }
        } else {
            throw new RuntimeException("batch import call failed: " + response.getStatusCode());
        }
    }

    public int createBPOiVueitServiceUsageRow(String propertyId, String address, String city, String state, String zipcode) {

        String sql = "INSERT INTO firstamerican.bpo_ivueit_service_usage (property_id, address, city, state, zipcode) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        prodJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, propertyId);
            ps.setString(2, address);
            ps.setString(3, city);
            ps.setString(4, state);
            ps.setString(5, zipcode);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("id")) {
            return ((Number) keys.get("id")).intValue();
        } else {
            throw new RuntimeException("Failed to retrieve generated id from insert");
        }
    }

    public void updateBPOiVueitRequestRowVueId(int id, String vueId) {
        String sql = "UPDATE firstamerican.bpo_ivueit_service_usage SET vue_id = ? WHERE id = ?";
        prodJdbcTemplate.update(sql, vueId, id);
    }

    public long getPublishAtNanos() {
        return Instant.now().toEpochMilli() * 1_000_000;
    }

    public List<String> getIVueitImages(String vueId) {

        String query = "SELECT images FROM firstamerican.bpo_ivueit_service_usage WHERE vue_id = ?";

        String images = prodBackupJdbcTemplate.queryForObject(query, new Object[]{vueId}, String.class);

        if (images == null || images.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.asList(images.split("\\s*,\\s*"));
    }

    public String getSubmissionIdIfExistsByVueId(String vueId) {

        String url = "https://prod.data.ivueit.network/api/v1/vue/" + vueId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x_ivueit_auth_token", getAccessTokenIVueitAPI());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode submissionIdNode = mapper.readTree(response.getBody()).get("submissionId");
                if (submissionIdNode != null && !submissionIdNode.isNull()) {
                    updateBPOiVueitRequestRowSubmissionId(submissionIdNode.asText(), vueId);
                    return submissionIdNode.asText();
                } else {
                    throw new RuntimeException("submissionId not found in response");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse JSON response: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Failed to get Vue info: " + response.getStatusCode());
        }
    }

    public void updateBPOiVueitRequestRowSubmissionId(String submissionId, String vueId) {
        String sql = "UPDATE firstamerican.bpo_ivueit_service_usage SET submission_id = ? WHERE vue_id = ?";
        prodJdbcTemplate.update(sql, submissionId, vueId);
    }

    public void fetchImagesFromIVueitBySubmissionId(String submissionId) {

        String url = "https://prod.data.ivueit.network/api/v1/surveysubmission/" + submissionId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x_ivueit_auth_token", getAccessTokenIVueitAPI());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                List<String> urls = new ArrayList<>();
                JsonNode steps = root.get("steps");
                if (steps != null && steps.isArray()) {
                    for (JsonNode step : steps) {
                        String stepType = step.path("stepType").asText();
                        if ("PHOTO".equalsIgnoreCase(stepType)) {
                            JsonNode photos = step.get("photos");
                            if (photos != null && photos.isArray()) {
                                for (JsonNode photo : photos) {
                                    String urlVal = photo.path("url").asText(null);
                                    if (urlVal != null) {
                                        urls.add(urlVal);
                                    }
                                }
                            }
                        }
                    }
                }
                updateImagesForSubmissionId(submissionId, urls);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse JSON response: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Failed to fetch submission: " + response.getStatusCode());
        }
    }

    public void updateImagesForSubmissionId(String submissionId, List<String> imageUrls) {
        String joinedURLs = String.join(",", imageUrls);
        String sql = "UPDATE firstamerican.bpo_ivueit_service_usage SET images = ? WHERE submission_id = ?";
        prodJdbcTemplate.update(sql, joinedURLs, submissionId);
    }

    public void checkAndProcessIVueitRequests() {
        String query = "SELECT vue_id FROM firstamerican.bpo_ivueit_service_usage WHERE submission_id IS NULL";
        List<String> resultList = prodBackupJdbcTemplate.queryForList(query, String.class);
        for (String vueId : resultList) {
            String submissionId = getSubmissionIdIfExistsByVueId(vueId);
            if (submissionId != null) {fetchImagesFromIVueitBySubmissionId(submissionId);}
        }
    }
}