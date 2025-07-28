package com.broker.price.opinion.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

    public String createImageGroup(String address) {

        try {

            URL url = new URL("https://api.foxyai.com/image-group");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + foxyAIApiKey);
            connection.setDoOutput(true);

            String jsonInputString = String.format("{ \"name\": \"%s\", \"address\": \"%s\" }", address, address);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            String response;

            try (Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8")) {
                response = scanner.useDelimiter("\\A").next();
            }

            connection.disconnect();

            JSONObject json = new JSONObject(response);

            if (json.has("status") && "completed".equals(json.getString("status"))) {
                return json.getString("_id");
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String uploadImagesBatchUserGroup(String groupId, String[] imageUrls) {

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
            for (String imageUrl : imageUrls) {
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
                response = scanner.useDelimiter("\\A").next();
            }

            connection.disconnect();

            JSONObject json = new JSONObject(response);

            if (json.has("status") && "ok".equalsIgnoreCase(json.getString("status"))) {
                return "Success";
            } else {
                return "Fail";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
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

            try (InputStream inputStream = connection.getInputStream();
                 Scanner scanner = new Scanner(inputStream, "UTF-8")) {
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
}