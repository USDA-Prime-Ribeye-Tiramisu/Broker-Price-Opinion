package com.broker.price.opinion.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Service
public class FoxyAIService {

    @Value("${foxyai.api.key}")
    private String foxyAIApiKey;

    private final JdbcTemplate prodJdbcTemplate;
    private final JdbcTemplate prodBackupJdbcTemplate;

    @Autowired
    public FoxyAIService(
            @Qualifier("prodJdbcTemplate") JdbcTemplate prodJdbcTemplate,
            @Qualifier("prodBackupJdbcTemplate") JdbcTemplate prodBackupJdbcTemplate) {
        this.prodJdbcTemplate = prodJdbcTemplate;
        this.prodBackupJdbcTemplate = prodBackupJdbcTemplate;
    }

    public void createImageGroup(int bpoId, String address) {

        int recordId = createBPOFoxyAIServiceUsageRow(bpoId, address);

        try {

            URL url = new URL("https://api.foxyai.com/image-group");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + foxyAIApiKey);
            connection.setDoOutput(true);

            String jsonInputString = String.format("{ \"name\": \"%s\", \"address\": \"%s\" }", address, address);

            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            String response;
            try (Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8")) {
                response = scanner.useDelimiter("\\A").next();
            }

            connection.disconnect();

            JSONObject json = new JSONObject(response);

            if ("completed".equals(json.optString("status")) && json.optString("_id") != null) {
                updateImagesGroupCreationStatusById(recordId, "Done", json.optString("_id"));
            } else {
                updateImagesGroupCreationStatusById(recordId, "Failed", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadImagesBatchUserGroup(int recordId, String groupId, List<String> imageURLs) {

        imageURLs.add("https://dvvjkgh94f2v6.cloudfront.net/7b68097c/389664914/83dcefb7.jpeg");
        imageURLs.add("https://dvvjkgh94f2v6.cloudfront.net/7b68097c/389664914/1ad5be0d.jpeg");
        imageURLs.add("https://dvvjkgh94f2v6.cloudfront.net/7b68097c/389664914/6dd28e9b.jpeg");
        imageURLs.add("https://dvvjkgh94f2v6.cloudfront.net/7b68097c/389664914/f3b61b38.jpeg");
        imageURLs.add("https://dvvjkgh94f2v6.cloudfront.net/7b68097c/389664914/d8819d45.jpeg");

        try {

            URL url = new URL("https://api.foxyai.com/image/batch");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + foxyAIApiKey);
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("imageGroups", new JSONArray().put(groupId));

            JSONArray imagesArray = new JSONArray();
            for (String imageUrl : imageURLs) {
                JSONObject imageObject = new JSONObject();
                imageObject.put("url", imageUrl);
                imageObject.put("models", new JSONArray().put("condition_score"));
                imagesArray.put(imageObject);
            }
            requestBody.put("images", imagesArray);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            String response;
            try (Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8")) {
                response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            }

            connection.disconnect();

            JSONObject json = new JSONObject(response);

            JSONArray imagesArrayResponse = json.optJSONArray("images");

            boolean allProcessing = true;

            if (imagesArrayResponse != null) {
                for (int i = 0; i < imagesArrayResponse.length(); i++) {
                    JSONObject image = imagesArrayResponse.getJSONObject(i);
                    if (!"processing".equalsIgnoreCase(image.optString("status"))) {
                        allProcessing = false;
                    }
                }
            }

            if (allProcessing) {
                updateBatchUploadStatusById(recordId, "Done", String.join(",", imageURLs));
            } else {
                updateBatchUploadStatusById(recordId, "Failed", String.join(",", imageURLs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Double> getConditionScores(String groupId) {

        List<Double> conditionScores = new ArrayList<>();

        try {
            URL url = new URL("https://api.foxyai.com/image-group/" + groupId + "/results");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + foxyAIApiKey);

            String response;
            try (Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8")) {
                response = scanner.useDelimiter("\\A").next();
            }

            connection.disconnect();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray imagesArray = jsonObject.getJSONArray("images");

            for (int i = 0; i < imagesArray.length(); i++) {
                JSONObject image = imagesArray.getJSONObject(i);
                JSONObject models = image.optJSONObject("models");
                if (models != null) {
                    JSONObject conditionScore = models.optJSONObject("condition_score");
                    if (conditionScore != null && "completed".equalsIgnoreCase(conditionScore.optString("status"))) {
                        JSONArray results = conditionScore.optJSONArray("results");
                        if (results != null) {
                            for (int j = 0; j < results.length(); j++) {
                                JSONObject result = results.getJSONObject(j);
                                if ("SCORE".equalsIgnoreCase(result.optString("annotationType"))) {
                                    conditionScores.add(result.optDouble("score"));
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conditionScores;
    }

    public Integer createBPOFoxyAIServiceUsageRow(int bpoId, String address) {

        String sql = "INSERT INTO firstamerican.bpo_foxyai_service_usage (bpo_id, address, group_name) " +
                "VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        prodJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, bpoId);
            ps.setString(2, address);
            ps.setString(3, address);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("id")) {
            return ((Number) keys.get("id")).intValue();
        } else {
            throw new RuntimeException("Failed to retrieve generated id");
        }
    }

    public void updateImagesGroupCreationStatusById(int id, String status, String groupId) {

        String sql = "UPDATE firstamerican.bpo_foxyai_service_usage " +
                "SET images_group_creation_status = ?, group_id = ? " +
                "WHERE id = ?";

        prodJdbcTemplate.update(sql, status, groupId, id);
    }

    public void updateBatchUploadStatusById(int id, String batchUploadStatus, String imageURLs) {

        String sql = "UPDATE firstamerican.bpo_foxyai_service_usage " +
                "SET batch_upload_status = ?, images_urls = ? " +
                "WHERE id = ?";

        prodJdbcTemplate.update(sql, batchUploadStatus, imageURLs, id);
    }
}